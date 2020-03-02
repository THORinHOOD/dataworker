package com.thorinhood.dataworker.loaders;

import com.thorinhood.dataworker.repositories.TwitterTableRepo;
import com.thorinhood.dataworker.repositories.TwitterUnindexedTableRepo;
import com.thorinhood.dataworker.services.TwitterService;
import com.thorinhood.dataworker.services.db.TwitterDBService;
import com.thorinhood.dataworker.tables.TwitterTable;
import com.thorinhood.dataworker.tables.TwitterUnindexedTable;
import org.springframework.scheduling.annotation.Scheduled;

public class TwitterLoader extends CommonLoader<TwitterDBService, TwitterTableRepo, TwitterUnindexedTableRepo,
        TwitterTable, TwitterUnindexedTable, String, String> {

    public TwitterLoader(TwitterDBService dbService, TwitterService twitterService) {
        super(dbService, twitterService);
    }

    @Scheduled(fixedRate = 10000000)
    @Override
    public void loadData() {
        super.loadData();
    }

}
