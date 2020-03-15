package com.thorinhood.dataworker.loaders;

import com.google.common.collect.Lists;
import com.thorinhood.dataworker.services.SocialService;
import com.thorinhood.dataworker.db.DBService;
import com.thorinhood.dataworker.tables.FriendsPair;
import com.thorinhood.dataworker.tables.FriendsPrimaryKey;
import com.thorinhood.dataworker.tables.Profile;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CommonLoader<
        DB extends DBService<TABLEREPO, FRIENDSREPO, TABLE, ID, FRIENDS_TABLE>,
        TABLEREPO extends CassandraRepository<TABLE, ID>,
        FRIENDSREPO extends CassandraRepository<FRIENDS_TABLE, FriendsPrimaryKey>,
        TABLE extends Profile<ID, FRIENDS_TABLE>,
        ID,
        FRIENDS_TABLE extends FriendsPair> {

    private final static int THREADS_COUNT = 30;
    protected final Logger logger;
    protected final DB dbService;
    protected final SocialService<TABLE, ID, FRIENDS_TABLE> service;
    private final Class loaderClass;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(THREADS_COUNT);

    public CommonLoader(DB dbService, SocialService<TABLE, ID, FRIENDS_TABLE> service, Class loaderClass) {
        this.dbService = dbService;
        this.service = service;
        this.loaderClass = loaderClass;
        logger = LoggerFactory.getLogger(loaderClass);
    }

    public List<ID> loadData(List<ID> ids) {
        logger.info("Started to load and save profiles : " + ids.size());
        List<ID> result = Lists.partition(ids, 25).stream()
            .flatMap(partition -> {
                List<TABLE> users = service.getUsersInfo(partition);
                List<ID> friends = users.stream()
                        .peek(x -> dbService.saveProfiles(Collections.singletonList(x)))
                        .flatMap(x -> {
                            if (CollectionUtils.isNotEmpty(x.getLinked())) {
                                return x.getLinked().stream();
                            }
                            return Stream.empty();
                        })
                        .collect(Collectors.toList());
                return friends.stream();
            })
            .collect(Collectors.toList());
        logger.info("Loaded and saved profiles : " + ids.size());
        return result;
//        List<Future<Collection<TABLE>>> futures = Lists.partition(ids, countBatches).stream()
//                .map(batch -> threadPool.submit(() -> service.getUsersInfo(batch)))
//                .collect(Collectors.toList());

//        return futures.stream()
//            .flatMap(future -> {
//                try {
//                    Collection<TABLE> batch = future.get();
//                    int count = batch.size() / 50;
//                    if (count == 0) {
//                        count = 1;
//                    }
//                    Lists.partition(new ArrayList<>(batch), count).forEach(dbService::saveProfiles);
//                    logger.info("Saved profiles batch : " + batch.size());
//                    return batch.stream().flatMap(x -> {
//                        if (CollectionUtils.isNotEmpty(x.getLinked())) {
//                            return x.getLinked().stream();
//                        }
//                        return Stream.empty();
//                    });
//                } catch (InterruptedException | ExecutionException e) {
//                    logger.error("Error while getting future", e);
//                }
//                return Stream.empty();
//            })
//            .collect(Collectors.toList());
    }

}
