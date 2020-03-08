package com.thorinhood.dataworker.loaders;

import com.google.common.collect.Lists;
import com.thorinhood.dataworker.services.SocialService;
import com.thorinhood.dataworker.services.db.DBService;
import com.thorinhood.dataworker.tables.HasId;
import com.thorinhood.dataworker.tables.Profile;
import com.thorinhood.dataworker.tables.VKUnindexedTable;
import com.thorinhood.dataworker.utils.common.BatchProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CommonLoader<DB extends DBService<TABLEREPO, UNTABLEREPO, TABLE, UNTABLE, ID>,
                                   TABLEREPO extends CassandraRepository<TABLE, ID>,
                                   UNTABLEREPO extends CassandraRepository<UNTABLE, ID>,
                                   TABLE extends Profile<ID>,
                                   UNTABLE extends HasId<ID>,
                                   ID> {
    private final static int THREADS_COUNT = 10;
    protected final Logger logger;
    protected final DB dbService;
    protected final SocialService<TABLE, ID> service;
    private final Class loaderClass;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(THREADS_COUNT);

    public CommonLoader(DB dbService, SocialService<TABLE, ID> service, Class loaderClass) {
        this.dbService = dbService;
        this.service = service;
        this.loaderClass = loaderClass;
        logger = LoggerFactory.getLogger(loaderClass);
    }

    public void loadData(List<ID> ids) {
        List<TABLE> users = Lists.partition(ids, ids.size()/THREADS_COUNT).stream()
            .map(batch -> threadPool.submit(() -> service.getUsersInfo(batch)))
            .flatMap(future -> {
                Collection<TABLE> batch = Collections.emptyList();
                try {
                    batch = future.get();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error while getting future", e);
                }
                return batch.stream();
            })
            .collect(Collectors.toList());
        dbService.saveProfiles(users);
        dbService.saveUnindexed(users.stream()
                .flatMap(profile -> profile.getLinked().stream())
                .distinct()
                .collect(Collectors.toList()));
    }

}
