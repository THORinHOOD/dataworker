package com.thorinhood.dataworker.db;

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.Lists;
import com.thorinhood.dataworker.repositories.related.RelatedTableRepo;
import com.thorinhood.dataworker.tables.friends.FriendsPair;
import com.thorinhood.dataworker.tables.friends.FriendsPrimaryKey;
import com.thorinhood.dataworker.tables.profile.Profile;
import com.thorinhood.dataworker.tables.related.RelatedTable;
import com.thorinhood.dataworker.utils.common.Finder;
import com.thorinhood.dataworker.utils.common.MeasureTimeUtil;
import com.thorinhood.dataworker.utils.common.MultiTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class DBService<TABLE_REPO extends CassandraRepository<TABLE, ID>,
                                FRIENDS_REPO extends CassandraRepository<TABLE_FRIENDS, FriendsPrimaryKey>,
                                POSTS_REPO extends CassandraRepository<TABLE_POSTS, Long>,
                                TABLE extends Profile<ID, TABLE_FRIENDS>,
                                TABLE_POSTS,
                                ID,
                                TABLE_FRIENDS extends FriendsPair> {

    private static final Collection<BiFunction<RelatedTableRepo, RelatedTable, RelatedTable>> findExistFunctions = Arrays.asList(
        (repo, table) -> Finder.findByStringValue(repo, table, RelatedTable::getVkId, RelatedTableRepo::findByVkId),
        (repo, table) -> Finder.findByStringValue(repo, table, RelatedTable::getVkDomain, RelatedTableRepo::findByVkDomain),
        (repo, table) -> Finder.findByStringValue(repo, table, RelatedTable::getTwitter, RelatedTableRepo::findByTwitter),
        (repo, table) -> Finder.findByStringValue(repo, table, RelatedTable::getFacebook, RelatedTableRepo::findByFacebook),
        (repo, table) -> Finder.findByStringValue(repo, table, RelatedTable::getInstagram, RelatedTableRepo::findByInstagram)
    );

    protected final Logger logger;
    protected final TABLE_REPO tableRepo;
    protected final POSTS_REPO postsRepo;
    protected final CassandraTemplate cassandraTemplate;
    protected final String unindexedTable;
    protected final String needFriendsTable;
    protected final Class<ID> idClass;
    protected final RelatedTableRepo relatedTableRepo;
    protected final FRIENDS_REPO friendsRepo;
    protected final List<Consumer<Collection<String>>> saveProfilesEvents;
    protected final ExecutorService friendsExecutorService;
    protected final ExecutorService postsExecutorService;
    protected final int friendsThreads;
    protected final int postsThreads;
    protected final MeasureTimeUtil measureTimeUtil = new MeasureTimeUtil();

    public DBService(TABLE_REPO tableRepo,
                     FRIENDS_REPO friendsRepo,
                     POSTS_REPO postsRepo,
                     CassandraTemplate cassandraTemplate,
                     String unindexedTable,
                     String needFriendsTable,
                     Class<ID> idClass,
                     RelatedTableRepo relatedTableRepo,
                     Class dbServiceClass,
                     int friendsThreads,
                     int postsThreads) {
        this.cassandraTemplate = cassandraTemplate;
        this.tableRepo = tableRepo;
        this.unindexedTable = unindexedTable;
        this.needFriendsTable = needFriendsTable;
        this.idClass = idClass;
        this.relatedTableRepo = relatedTableRepo;
        logger = LoggerFactory.getLogger(dbServiceClass);
        this.friendsRepo = friendsRepo;
        this.postsRepo = postsRepo;
        this.saveProfilesEvents = new ArrayList<>();
        this.friendsThreads = friendsThreads;
        this.postsThreads = postsThreads;
        friendsExecutorService = Executors.newFixedThreadPool(friendsThreads);
        postsExecutorService = Executors.newFixedThreadPool(postsThreads);
    }

    public Slice<TABLE> findAll(Pageable pageable) {
        return tableRepo.findAll(pageable);
    }

    public void subscribeOnSave(Consumer<Collection<String>> onSave) {
        saveProfilesEvents.add(onSave);
    }

    private void handleSaveEvent(Collection<TABLE> savedTables) {
        Collection<String> ids = savedTables.stream()
                .map(Profile::getId)
                .collect(Collectors.toList());
        saveProfilesEvents.forEach(eventHandler -> eventHandler.accept(ids));
    }

    public void savePosts(List<TABLE_POSTS> posts) {
        MultiTool.executeNotEmptyWithMeasure(postsList -> {
            List<Future> futures = Lists.partition(postsList, Math.max(postsList.size()/postsThreads, postsList.size()))
                    .stream()
                    .map(x -> postsExecutorService.submit(() -> postsRepo.saveAll(x)))
                    .collect(Collectors.toList());
            futures.forEach(x -> {
                try {
                    x.get();
                } catch (Exception e) {
                    logger.error("Error [posts in future saving]");
                }
            });
        }, posts, "posts saving", measureTimeUtil, logger);
    }

    public void saveProfiles(List<TABLE> profiles) {
        MultiTool.partition(profiles, 100, batch -> {
            measureTimeUtil.measure(() -> tableRepo.saveAll(batch), logger, "profiles saving", batch.size());
            List<TABLE_FRIENDS> extractedFriends = batch.stream()
                    .flatMap(profile -> profile.generatePairs().stream())
                    .collect(Collectors.toList());
            MultiTool.executeNotEmptyWithMeasure(friends -> {
                List<Future> futures = Lists.partition(friends, Math.max(friends.size() / friendsThreads, friends.size()))
                        .stream()
                        .map(x -> friendsExecutorService.submit(() -> friendsRepo.saveAll(x)))
                        .collect(Collectors.toList());
                futures.forEach(x -> {
                    try {
                        x.get();
                    } catch (InterruptedException | ExecutionException e) {
                        logger.error("Error [friends in future saving]", e);
                    }
                });
            }, extractedFriends, "friends saving", measureTimeUtil, logger);
            Collection<RelatedTable> relatedTables = actualize(convert(batch));
            measureTimeUtil.measure(() -> relatedTableRepo.saveAll(relatedTables), logger, "related",
                    relatedTables.size());
        });
        MultiTool.executeNotEmpty(data -> measureTimeUtil.measure(() -> handleSaveEvent(data), logger,
            "handle profiles saving", profiles.size()), profiles);
    }

    public Collection<RelatedTable> actualize(Collection<RelatedTable> tables) {
        return tables.stream()
            .map(this::getExists)
            .collect(Collectors.toList());
    }

    private RelatedTable getExistsRelatedTable(RelatedTable relatedTable) {
        RelatedTable result;
        for (BiFunction<RelatedTableRepo, RelatedTable, RelatedTable> finder : findExistFunctions) {
            result = finder.apply(relatedTableRepo, relatedTable);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public RelatedTable getExists(RelatedTable toSave) {
        RelatedTable relatedTable = getExistsRelatedTable(toSave);
        if (relatedTable == null) {
            return toSave;
        }
        relatedTable.setFacebook(toSave.getFacebook());
        relatedTable.setTwitter(toSave.getTwitter());
        relatedTable.setFacebook(toSave.getFacebook());
        relatedTable.setVkDomain(toSave.getVkDomain());
        relatedTable.setVkId(toSave.getVkId());
        return relatedTable;
    }

    protected Collection<RelatedTable> convert(Collection<TABLE> tables) {
        return tables.stream()
                .map(table -> new RelatedTable()
                    .setUid(UUIDs.timeBased())
                    .setTwitter(table.twitter())
                    .setFacebook(table.facebook())
                    .setInstagram(table.instagram())
                    .setVkId(table.vkId())
                    .setVkDomain(table.vkDomain()))
                .filter(x -> !x.isEmpty())
                .collect(Collectors.toList());
    }

    private String batch(Collection<ID> ids) {
        return ids.stream().map(x -> "(\'" + x + "\')").collect(Collectors.joining(","));
    }

    public long countAllProfiles() {
        return tableRepo.count();
    }

    public long countFriends() {
        return friendsRepo.count();
    }

    public long countPosts() {
        return postsRepo.count();
    }

    public void truncateAll() {
        tableRepo.deleteAll();
        relatedTableRepo.deleteAll();
        friendsRepo.deleteAll();
        postsRepo.deleteAll();
    }

    public Optional<TABLE> getPageById(ID id) {
        return tableRepo.findById(id);
    }

    public boolean containsPage(ID id) {
        return tableRepo.existsById(id);
    }

}
