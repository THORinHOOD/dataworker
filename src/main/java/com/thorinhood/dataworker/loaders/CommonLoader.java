package com.thorinhood.dataworker.loaders;

import com.thorinhood.dataworker.services.SocialService;
import com.thorinhood.dataworker.services.db.DBService;
import com.thorinhood.dataworker.tables.HasId;
import com.thorinhood.dataworker.tables.Profile;
import com.thorinhood.dataworker.utils.common.BatchProfiles;
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
import java.util.stream.Collectors;

public abstract class CommonLoader<DB extends DBService<TABLEREPO, UNTABLEREPO, TABLE, UNTABLE, ID, UNID>,
                                   TABLEREPO extends CassandraRepository<TABLE, ID>,
                                   UNTABLEREPO extends CassandraRepository<UNTABLE, UNID>,
                                   TABLE extends Profile<ID>,
                                   UNTABLE extends HasId<UNID>,
                                   ID, UNID> {

    protected final DB dbService;
    protected final SocialService<TABLE, ID> service;
    protected BlockingQueue<BatchProfiles<TABLE, ID>> profilesQueue;


    public CommonLoader(DB dbService, SocialService<TABLE, ID> service) {
        this.dbService = dbService;
        this.service = service;
        profilesQueue = new LinkedBlockingQueue<>();
    }

    public void loadData(List<ID> ids) {
        ExecutorService threadPool = Executors.newFixedThreadPool(ids.size() + 1);
        Future<?> futureDB = threadPool.submit(() -> dbService.savePagesProcess(profilesQueue, ids.size()));
        ids.stream()
            .map(id -> threadPool.submit(
                () -> service.getDefaultUsersInfo(Collections.singletonList(id), profilesQueue)
            ))
            .forEach(future -> {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        try {
            futureDB.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        threadPool.shutdownNow();
        System.out.println("Ending loading data...");
    }

//    public void loadData() {
//       // List<UNID> unindexedPages = dbService.getAllUnindexedPages();
//        if (!CollectionUtils.isEmpty(unindexedPages)) {
//            Collection<TABLE> users = service.getDefaultUsersInfo(unindexedPages.stream()
//                    .map(String::valueOf)
//                    .collect(Collectors.toList()));
//            dbService.savePages(users);
//        }
//    }

}
