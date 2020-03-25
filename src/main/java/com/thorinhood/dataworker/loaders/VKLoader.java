package com.thorinhood.dataworker.loaders;

import com.thorinhood.dataworker.repositories.friends.VKFriendsTableRepo;
import com.thorinhood.dataworker.repositories.posts.VKPostsTableRepo;
import com.thorinhood.dataworker.repositories.profiles.VKTableRepo;
import com.thorinhood.dataworker.services.social.VKService;
import com.thorinhood.dataworker.db.VKDBService;
import com.thorinhood.dataworker.tables.friends.VKFriendsTable;
import com.thorinhood.dataworker.tables.posts.VKPostsTable;
import com.thorinhood.dataworker.tables.profile.VKTable;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VKLoader extends CommonLoader<VKDBService, VKTableRepo, VKFriendsTableRepo, VKPostsTableRepo, VKTable,
        VKPostsTable, String, VKFriendsTable> {

    public VKLoader(VKDBService dbService, VKService vkService) {
        super(dbService, vkService, VKLoader.class);
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

        List<String> next = Collections.singletonList("thorinhoodie");
        int depth = 10;
        while (depth > 0) {
            next = super.loadData(next);
            depth--;
        }
//        VKService vkService = (VKService) service;
//        for (;;) {
//            try {
//                GetResponse response = vkService.getVK().domain("thorinhoodie").execute();
//                logger.info(response.toString());
//            } catch (ApiException | ClientException e) {
//                e.printStackTrace();
//            }
//        }
    }

}
