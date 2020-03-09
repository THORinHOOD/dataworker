package com.thorinhood.dataworker.services.db;

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.Lists;
import com.thorinhood.dataworker.repositories.RelatedTableRepo;
import com.thorinhood.dataworker.tables.HasId;
import com.thorinhood.dataworker.tables.Profile;
import com.thorinhood.dataworker.tables.RelatedTable;
import com.thorinhood.dataworker.utils.common.Finder;
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
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class DBService<TABLEREPO extends CassandraRepository<TABLE, ID>,
                                UNINDEXEDREPO extends CassandraRepository<UNTABLE, ID>,
                                TABLE extends Profile<ID>,
                                UNTABLE extends HasId<ID>,
                                ID> {

    private static final Collection<BiFunction<RelatedTableRepo, RelatedTable, RelatedTable>> findExistFunctions = Arrays.asList(
        (repo, table) -> Finder.findByStringValue(repo, table, RelatedTable::getVkId, RelatedTableRepo::findByVkId),
        (repo, table) -> Finder.findByStringValue(repo, table, RelatedTable::getVkDomain, RelatedTableRepo::findByVkDomain),
        (repo, table) -> Finder.findByStringValue(repo, table, RelatedTable::getTwitter, RelatedTableRepo::findByTwitter),
        (repo, table) -> Finder.findByStringValue(repo, table, RelatedTable::getFacebook, RelatedTableRepo::findByFacebook),
        (repo, table) -> Finder.findByStringValue(repo, table, RelatedTable::getInstagram, RelatedTableRepo::findByInstagram)
    );

    protected final Logger logger;
    protected final TABLEREPO tableRepo;
    protected final UNINDEXEDREPO unindexedRepo;
    protected final CassandraTemplate cassandraTemplate;
    protected final String unindexedTable;
    protected final String needFriendsTable;
    protected final Class<ID> idClass;
    protected final RelatedTableRepo relatedTableRepo;
    protected final Function<ID, UNTABLE> createUnindexedTable;
    protected final JdbcTemplate postgresJdbc;

    public DBService(TABLEREPO tableRepo,
                     UNINDEXEDREPO unindexedRepo,
                     CassandraTemplate cassandraTemplate,
                     String unindexedTable,
                     String needFriendsTable,
                     Class<ID> idClass,
                     RelatedTableRepo relatedTableRepo,
                     Function<ID, UNTABLE> createUnindexedTable,
                     JdbcTemplate postgresJdbc,
                     Class dbServiceClass) {
        this.cassandraTemplate = cassandraTemplate;
        this.tableRepo = tableRepo;
        this.unindexedTable = unindexedTable;
        this.unindexedRepo = unindexedRepo;
        this.needFriendsTable = needFriendsTable;
        this.idClass = idClass;
        this.relatedTableRepo = relatedTableRepo;
        logger = LoggerFactory.getLogger(dbServiceClass);
        this.createUnindexedTable = createUnindexedTable;
        this.postgresJdbc = postgresJdbc;
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

//    public List<ID> getAllUnindexedPages() {
//        return cassandraTemplate.getCqlOperations().queryForList("SELECT id FROM " + unindexedTable, idClass);
//    }

    public void saveProfiles(Collection<TABLE> profiles) {
        Lists.partition(new ArrayList<>(profiles), 200).forEach(partition -> {
            tableRepo.saveAll(partition.stream()
                    .filter(table -> !tableRepo.existsById(table.id()))
                    .collect(Collectors.toList()));
            relatedTableRepo.saveAll(actualize(convert(partition)));
        });
    }

//    public void savePagesProcess(BlockingQueue<BatchProfiles<TABLE, ID>> queue, int threads) {
//        logger.info("Start to receive profiles batches...");
//        int current = threads;
//        try {
//            while (current > 0) {
//                logger.info("Waiting next batch...");
//                BatchProfiles<TABLE, ID> batchProfiles = queue.take();
//                if (batchProfiles.isEnd()) {
//                    current--;
//                    logger.info(String.format("Current progress : %d/%d", threads - current, threads));
//                } else {
//                    Collection<TABLE> tables = batchProfiles.getProfiles();
//                    tableRepo.saveAll(tables.stream()
//                            .filter(table -> !tableRepo.existsById(table.id()))
//                            .collect(Collectors.toList()));
//                    relatedTableRepo.saveAll(actualize(convert(tables)));
//                }
//                logger.info("Saved batch...");
//            }
//        } catch (Exception e) {
//            logger.error("DB failed", e);
//        }
//        logger.info("Ended to receive profiles batches...");
//    }

    public Collection<RelatedTable> actualize(Collection<RelatedTable> tables) {
        return tables.stream()
            .map(this::getExists)
            .collect(Collectors.toList());
    }

    private RelatedTable getExistsRelatedTable(RelatedTable relatedTable) {
        RelatedTable result = null;
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
                .collect(Collectors.toList());
    }

    private String batch(Collection<ID> ids) {
        return ids.stream().map(x -> "(\'" + x + "\')").collect(Collectors.joining(","));
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
