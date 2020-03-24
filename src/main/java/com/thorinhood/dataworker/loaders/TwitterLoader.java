package com.thorinhood.dataworker.loaders;

import com.thorinhood.dataworker.repositories.friends.TwitterFriendsTableRepo;
import com.thorinhood.dataworker.repositories.posts.TwitterPostsTableRepo;
import com.thorinhood.dataworker.repositories.profiles.TwitterTableRepo;
import com.thorinhood.dataworker.services.TwitterService;
import com.thorinhood.dataworker.db.TwitterDBService;
import com.thorinhood.dataworker.tables.friends.TwitterFriendsTable;
import com.thorinhood.dataworker.tables.posts.TwitterPostsTable;
import com.thorinhood.dataworker.tables.profile.TwitterTable;
import org.springframework.scheduling.annotation.Scheduled;

public class TwitterLoader extends CommonLoader<TwitterDBService, TwitterTableRepo, TwitterFriendsTableRepo,
        TwitterPostsTableRepo, TwitterTable, TwitterPostsTable, String, TwitterFriendsTable> {

    public TwitterLoader(TwitterDBService dbService, TwitterService twitterService) {
        super(dbService, twitterService, TwitterLoader.class);
    }

    @Scheduled(fixedDelay = Long.MAX_VALUE)
    public void loadData() {
//        TwitterService twitterService = (TwitterService) service;
//        for (;;) {
//            try {
//                Thread.sleep(20L);
//                logger.info("tweets -> " + twitterService.getTwitter().timelineOperations().getUserTimeline("k160rg").toString());
////                logger.info(""twitterService.getTwitter().friendOperations().getFriendIds("k160rg").toString());
//            } catch(Exception exception) {
//                logger.error("ERROR", exception);
//            }
//        }
//        TwitterService twitterService = (TwitterService) service;
//        long id = twitterService.getTwitter().userOperations().getUsers("k160rg").get(0).getId();
//        logger.info("My id is : " + id);
//        logger.info("GOT : " + dbService.findTwitterProfilesFromRelated());
    }

}
