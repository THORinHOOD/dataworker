package com.thorinhood.dataworker.loaders;

import com.google.common.collect.Lists;
import com.thorinhood.dataworker.services.SocialService;
import com.thorinhood.dataworker.services.db.DBService;
import com.thorinhood.dataworker.tables.HasId;
import com.thorinhood.dataworker.tables.Profile;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

    public List<ID> loadData(List<ID> ids) {
        int countBatches = ids.size() / THREADS_COUNT;
        if (countBatches < 1) {
            countBatches = 1;
        }
        List<Future<Collection<TABLE>>> futures = Lists.partition(ids, countBatches).stream()
                .map(batch -> threadPool.submit(() -> service.getUsersInfo(batch)))
                .collect(Collectors.toList());

        List<TABLE> users = futures.stream()
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
        return users.stream()
                .flatMap(profile -> {
                    if (CollectionUtils.isEmpty(profile.getLinked())) {
                        return Stream.empty();
                    }
                    return profile.getLinked().stream();
                })
                .distinct()
                .collect(Collectors.toList());
    }

}
