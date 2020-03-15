package com.thorinhood.dataworker.services.db;

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.Lists;
import com.thorinhood.dataworker.repositories.RelatedTableRepo;
import com.thorinhood.dataworker.tables.FriendsPair;
import com.thorinhood.dataworker.tables.FriendsPairsGenerator;
import com.thorinhood.dataworker.tables.FriendsPrimaryKey;
import com.thorinhood.dataworker.tables.HasId;
import com.thorinhood.dataworker.tables.Profile;
import com.thorinhood.dataworker.tables.RelatedTable;
import com.thorinhood.dataworker.tables.VKFriendsTable;
import com.thorinhood.dataworker.tables.VKTable;
import com.thorinhood.dataworker.utils.common.Finder;
import com.thorinhood.dataworker.utils.common.MeasureTimeUtil;
import com.vk.api.sdk.actions.Friends;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class DBService<TABLEREPO extends CassandraRepository<TABLE, ID>,
                                FRIENDSREPO extends CassandraRepository<TABLE_FRIENDS, FriendsPrimaryKey>,
                                TABLE extends Profile<ID, TABLE_FRIENDS>,
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
    protected final TABLEREPO tableRepo;
    protected final CassandraTemplate cassandraTemplate;
    protected final String unindexedTable;
    protected final String needFriendsTable;
    protected final Class<ID> idClass;
    protected final RelatedTableRepo relatedTableRepo;
    protected final JdbcTemplate postgresJdbc;
    protected final FRIENDSREPO friendsRepo;
    protected final List<Consumer<Collection<String>>> saveProfilesEvents;

    public DBService(TABLEREPO tableRepo,
                     FRIENDSREPO friendsRepo,
                     CassandraTemplate cassandraTemplate,
                     String unindexedTable,
                     String needFriendsTable,
                     Class<ID> idClass,
                     RelatedTableRepo relatedTableRepo,
                     JdbcTemplate postgresJdbc,
                     Class dbServiceClass) {
        this.cassandraTemplate = cassandraTemplate;
        this.tableRepo = tableRepo;
        this.unindexedTable = unindexedTable;
        this.needFriendsTable = needFriendsTable;
        this.idClass = idClass;
        this.relatedTableRepo = relatedTableRepo;
        logger = LoggerFactory.getLogger(dbServiceClass);
        this.postgresJdbc = postgresJdbc;
        this.friendsRepo = friendsRepo;
        this.saveProfilesEvents = new ArrayList<>();
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

    public void saveProfiles(Collection<TABLE> profiles) {
        final MeasureTimeUtil measureTimeUtil = new MeasureTimeUtil();
        Lists.partition(new ArrayList<>(profiles), 50).forEach(partition -> {
            measureTimeUtil.measure(() -> tableRepo.saveAll(partition), logger, "profiles saving");
            measureTimeUtil.measure(() -> handleSaveEvent(partition), logger, "handle profiles saving");
            measureTimeUtil.measure(() ->
                Lists.partition(partition.stream()
                        .flatMap(profile -> profile.generatePairs().stream())
                        .collect(Collectors.toList()), 50).forEach(friendsRepo::saveAll), logger, "friends");
            measureTimeUtil.measure(() -> relatedTableRepo.saveAll(actualize(convert(partition))), logger,
                    "related");
        });
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
