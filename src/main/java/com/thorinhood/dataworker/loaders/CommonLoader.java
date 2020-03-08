package com.thorinhood.dataworker.loaders;

import com.thorinhood.dataworker.services.SocialService;
import com.thorinhood.dataworker.services.db.DBService;
import com.thorinhood.dataworker.tables.HasId;
import com.thorinhood.dataworker.tables.Profile;
import com.thorinhood.dataworker.utils.common.BatchProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class CommonLoader<DB extends DBService<TABLEREPO, UNTABLEREPO, TABLE, UNTABLE, ID, UNID>,
                                   TABLEREPO extends CassandraRepository<TABLE, ID>,
                                   UNTABLEREPO extends CassandraRepository<UNTABLE, UNID>,
                                   TABLE extends Profile<ID>,
                                   UNTABLE extends HasId<UNID>,
                                   ID, UNID> {

    protected final Logger logger;
    protected final DB dbService;
    protected final SocialService<TABLE, ID> service;
    protected BlockingQueue<BatchProfiles<TABLE, ID>> profilesQueue;
    private final Class loaderClass;

    public CommonLoader(DB dbService, SocialService<TABLE, ID> service, Class loaderClass) {
        this.dbService = dbService;
        this.service = service;
        profilesQueue = new LinkedBlockingQueue<>();
        this.loaderClass = loaderClass;
        logger = LoggerFactory.getLogger(loaderClass);
    }

    public void loadData(List<ID> ids) {
        logger.info("Start loading profiles : " + ids.toString());
        ExecutorService threadPool = Executors.newFixedThreadPool(ids.size() + 1);
        Future<?> futureDB = threadPool.submit(() -> dbService.savePagesProcess(profilesQueue, ids.size()));
        ids.stream()
            .map(id -> threadPool.submit(
                () -> service.getUsersInfo(Collections.singletonList(id), profilesQueue)
            ))
            .forEach(future -> {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error while getting future", e);
                }
            });
        try {
            futureDB.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error white getting db future", e);
        }
        threadPool.shutdownNow();
        logger.info("Ended loading profiles...");
    }

}
