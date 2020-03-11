package com.thorinhood.dataworker.loaders;

import com.thorinhood.dataworker.repositories.TwitterFriendsTableRepo;
import com.thorinhood.dataworker.repositories.TwitterTableRepo;
import com.thorinhood.dataworker.services.TwitterService;
import com.thorinhood.dataworker.services.db.TwitterDBService;
import com.thorinhood.dataworker.tables.TwitterFriendsPrimaryKey;
import com.thorinhood.dataworker.tables.TwitterFriendsTable;
import com.thorinhood.dataworker.tables.TwitterTable;

public class TwitterLoader extends CommonLoader<TwitterDBService, TwitterTableRepo, TwitterFriendsTableRepo,
        TwitterTable, String, TwitterFriendsTable, TwitterFriendsPrimaryKey> {

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
