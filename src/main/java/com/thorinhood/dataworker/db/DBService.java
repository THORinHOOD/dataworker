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
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    protected final JdbcTemplate postgresJdbc;
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
                     JdbcTemplate postgresJdbc,
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
        this.postgresJdbc = postgresJdbc;
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

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ID> takeUnindexedPages(int count) {
        try {
            if (count <= 0) {
                return Collections.emptyList();
            }
            List<ID> ids = postgresJdbc.queryForList("select * from " + unindexedTable + " limit " + count, idClass);
            if (ids.size() > 0) {
                postgresJdbc.execute("DELETE FROM " + unindexedTable + " WHERE id in (" + ids.stream()
                        .map(String::valueOf).map(x -> "\'" + x + "\'")
                        .collect(Collectors.joining(",")) + ")");
            }
            return ids;
        } catch (Exception ex) {
            logger.error("Failed to take unindexed pages", ex);
            return Collections.emptyList();
        }
    }

    private void handleSaveEvent(Collection<TABLE> savedTables) {
        Collection<String> ids = savedTables.stream()
                .map(Profile::getId)
                .collect(Collectors.toList());
        saveProfilesEvents.forEach(eventHandler -> eventHandler.accept(ids));
    }

    public void savePosts(List<TABLE_POSTS> posts) {
        measureTimeUtil.measure(() -> {
            List<Future> futures = Lists.partition(posts, Math.max(posts.size()/postsThreads, posts.size()))
                .stream()
                .map(x -> postsExecutorService.submit(() -> postsRepo.saveAll(x)))
                .collect(Collectors.toList());
            futures.forEach(x -> {
                try {
                    x.get();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error [posts in future saving]", e);
                }
            });
        }, logger, "posts saving", posts.size());
    }

    public void saveProfiles(Collection<TABLE> profiles) {
        Lists.partition(new ArrayList<>(profiles), 100).forEach(partition -> {
            measureTimeUtil.measure(() -> tableRepo.saveAll(partition), logger, "profiles saving", partition.size());
            List<TABLE_FRIENDS> friends = partition.stream()
                    .flatMap(profile -> profile.generatePairs().stream())
                    .collect(Collectors.toList());
            measureTimeUtil.measure(() -> {
                List<Future> futures = Lists.partition(friends, Math.max(friends.size()/friendsThreads, friends.size()))
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
            }, logger, "friends saving", friends.size());
            Collection<RelatedTable> relatedTables = actualize(convert(partition));
            measureTimeUtil.measure(() -> relatedTableRepo.saveAll(relatedTables), logger,
                    "related", relatedTables.size());
        });
        measureTimeUtil.measure(() -> handleSaveEvent(profiles), logger, "handle profiles saving", profiles.size());
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

    public void truncateAll() {
        tableRepo.deleteAll();
        relatedTableRepo.deleteAll();
        friendsRepo.deleteAll();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void saveUnindexed(Collection<ID> ids) {
        logger.info("Start saving unindexed : " + ids.size());
        try {
            if (CollectionUtils.isEmpty(ids)) {
                return;
            }
            try {
                postgresJdbc.update("INSERT INTO " + unindexedTable + " (id) VALUES " + batch(ids)  + " ON CONFLICT " +
                        "(id) DO NOTHING");
            } catch(Exception ex) {
                logger.error("Error while insert unindexed", ex);
            }
        } catch(Exception exception) {
            logger.error("Failed to save unindexed pages", exception);
            return;
        }
        logger.info("Saved unindexed : " + ids.size());
    }

    public Optional<TABLE> getPageById(ID id) {
        return tableRepo.findById(id);
    }

    public boolean containsPage(ID id) {
        return tableRepo.existsById(id);
    }

}
