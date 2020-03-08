package com.thorinhood.dataworker.loaders;

import com.thorinhood.dataworker.repositories.TwitterTableRepo;
import com.thorinhood.dataworker.repositories.TwitterUnindexedTableRepo;
import com.thorinhood.dataworker.services.TwitterService;
import com.thorinhood.dataworker.services.db.TwitterDBService;
import com.thorinhood.dataworker.tables.TwitterTable;
import com.thorinhood.dataworker.tables.TwitterUnindexedTable;

public class TwitterLoader extends CommonLoader<TwitterDBService, TwitterTableRepo, TwitterUnindexedTableRepo,
        TwitterTable, TwitterUnindexedTable, String> {

    public TwitterLoader(TwitterDBService dbService, TwitterService twitterService) {
        super(dbService, twitterService, TwitterLoader.class);
    }

//    @Scheduled(fixedDelay = 1 * 60 * 60 * 1000)
//    public void loadData() {
//        new Thread(() -> {
//
//        }).start();
//    }

}
