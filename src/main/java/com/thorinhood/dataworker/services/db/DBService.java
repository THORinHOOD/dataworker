package com.thorinhood.dataworker.services.db;

import com.datastax.driver.core.utils.UUIDs;
import com.thorinhood.dataworker.repositories.RelatedTableRepo;
import com.thorinhood.dataworker.tables.HasId;
import com.thorinhood.dataworker.tables.HasPagesLinks;
import com.thorinhood.dataworker.tables.Profile;
import com.thorinhood.dataworker.tables.RelatedTable;
import com.thorinhood.dataworker.utils.common.BatchProfiles;
import com.thorinhood.dataworker.utils.common.Finder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.CassandraRepository;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public abstract class DBService<TABLEREPO extends CassandraRepository<TABLE, ID>,
                                UNINDEXEDREPO extends CassandraRepository<UNTABLE, UNID>,
                                TABLE extends Profile<ID>,
                                UNTABLE extends HasId<UNID>,
                                ID, UNID> {

    private static final Collection<BiFunction<RelatedTableRepo, RelatedTable, RelatedTable>> findExistFunctions = Arrays.asList(
            (repo, table) -> Finder.findByLongValue(repo, table, RelatedTable::getVkId, RelatedTableRepo::findByVkId),
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
    protected final Class<UNID> unidClass;
    protected final RelatedTableRepo relatedTableRepo;

    public DBService(TABLEREPO tableRepo,
                     UNINDEXEDREPO unindexedRepo,
                     CassandraTemplate cassandraTemplate,
                     String unindexedTable,
                     String needFriendsTable,
                     Class<ID> idClass,
                     Class<UNID> unidClass,
                     RelatedTableRepo relatedTableRepo,
                     Class dbServiceClass) {
        this.cassandraTemplate = cassandraTemplate;
        this.tableRepo = tableRepo;
        this.unindexedTable = unindexedTable;
        this.unindexedRepo = unindexedRepo;
        this.needFriendsTable = needFriendsTable;
        this.idClass = idClass;
        this.unidClass = unidClass;
        this.relatedTableRepo = relatedTableRepo;
        logger = LoggerFactory.getLogger(dbServiceClass);
    }

    public List<UNID> getAllUnindexedPages() {
        return cassandraTemplate.getCqlOperations().queryForList("SELECT id FROM " + unindexedTable, unidClass);
    }

    public void savePagesProcess(BlockingQueue<BatchProfiles<TABLE, ID>> queue, int threads) {
        logger.info("Start to receive profiles batches...");
        int current = threads;
        try {
            while (current > 0) {
                logger.info("Waiting next batch...");
                BatchProfiles<TABLE, ID> batchProfiles = queue.take();
                if (batchProfiles.isEnd()) {
                    current--;
                    logger.info(String.format("Current progress : %d/%d", threads - current, threads));
                } else {
                    Collection<TABLE> tables = batchProfiles.getProfiles();
                    tableRepo.saveAll(tables.stream()
                            .filter(table -> !tableRepo.existsById(table.id()))
                            .collect(Collectors.toList()));
                    relatedTableRepo.saveAll(actualize(convert(tables)));
                }
                logger.info("Saved batch...");
            }
        } catch (Exception e) {
            logger.error("DB failed", e);
        }
        logger.info("Ended to receive profiles batches...");
    }

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

    public void saveUnindexed(Collection<UNTABLE> ids) {
        unindexedRepo.saveAll(ids.stream()
                .filter(untable -> !unindexedRepo.existsById(untable.id()))
                .collect(Collectors.toList()));
    }

    public Optional<TABLE> getPageById(ID id) {
        return tableRepo.findById(id);
    }

    public boolean containsPage(ID id) {
        return tableRepo.existsById(id);
    }

}
