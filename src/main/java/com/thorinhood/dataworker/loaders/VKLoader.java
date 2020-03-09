package com.thorinhood.dataworker.loaders;

import com.thorinhood.dataworker.repositories.RelatedTableRepo;
import com.thorinhood.dataworker.repositories.VKTableRepo;
import com.thorinhood.dataworker.repositories.VKUnindexedTableRepo;
import com.thorinhood.dataworker.services.VKFriendsService;
import com.thorinhood.dataworker.services.VKService;
import com.thorinhood.dataworker.services.db.VKDBService;
import com.thorinhood.dataworker.tables.VKTable;
import com.thorinhood.dataworker.tables.VKUnindexedTable;
import com.thorinhood.dataworker.utils.common.MeasureTimeUtil;
import org.json.simple.parser.ParseException;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.security.cert.CollectionCertStoreParameters;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VKLoader extends CommonLoader<VKDBService, VKTableRepo, VKUnindexedTableRepo, VKTable, VKUnindexedTable,
        String> {

    private RelatedTableRepo relatedTableRepo;

    public VKLoader(VKDBService dbService, VKService vkService, RelatedTableRepo relatedTableRepo) {
        super(dbService, vkService, VKLoader.class);
        this.relatedTableRepo = relatedTableRepo;
    }


    private List<List<String>> get(int countInBatch, int countBatches, String id) {
        return IntStream.range(0, countBatches).mapToObj(i ->
                IntStream.range(0, countInBatch).mapToObj(j -> id).collect(Collectors.toList()))
            .collect(Collectors.toList());
    }

    private List<String> get(int countInBatch, String id) {
        return IntStream.range(0, countInBatch).mapToObj(j -> id).collect(Collectors.toList());
    }

    @Scheduled(fixedDelay=Long.MAX_VALUE)
    public void loadData() {

//        VKFriendsService vkFriendsService = new VKFriendsService(35);
//        ExecutorService executorService = Executors.newFixedThreadPool(30);
//        MeasureTimeUtil measureTimeUtil = new MeasureTimeUtil();
//        measureTimeUtil.start();
//        super.loadData(get(200, "172252308"));
//        List<Future<Map<String, List<String>>>> result = get(800, 30, "172252308").stream()
//                .map(x -> executorService.submit(() -> {
//                    VKFriendsService vkFriendsService = new VKFriendsService(50);
//                    return vkFriendsService.getFriends(x);
//                }))
//                .collect(Collectors.toList());
//        result.stream().forEach(x -> {
//            try {
//                x.get();
//            } catch (InterruptedException | ExecutionException e) {
//                logger.error("ERROR", e);
//            }
//        });
//        logger.info("done " + measureTimeUtil.end("%d ms"));
//        List<Future<Map<String, List<String>>>> futures = get(1000, 30, "172252308").stream()
//                .map(batch -> executorService.submit(() -> vkFriendsService.getFriends(batch)))
//                .collect(Collectors.toList());
//        futures.stream().forEach(future -> {
//            try {
//                future.get();
//            } catch (InterruptedException | ExecutionException e) {
//                logger.error("ERROR", e);measureTimeUtil.end("Loaded friends for : %dms")
//            }
//        });

        List<String> next = Collections.singletonList("thorinhoodie");
        int depth = 10;
        while (depth > 0) {
            next = super.loadData(next);
            depth--;
        }
    }

}
