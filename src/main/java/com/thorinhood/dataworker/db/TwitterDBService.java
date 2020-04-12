package com.thorinhood.dataworker.db;

import com.thorinhood.dataworker.repositories.friends.TwitterFriendsTableRepo;
import com.thorinhood.dataworker.repositories.posts.TwitterPostsTableRepo;
import com.thorinhood.dataworker.repositories.profiles.TwitterTableRepo;
import com.thorinhood.dataworker.repositories.related.RelatedTableRepo;
import com.thorinhood.dataworker.tables.friends.TwitterFriendsTable;
import com.thorinhood.dataworker.tables.posts.TwitterPostsTable;
import com.thorinhood.dataworker.tables.profile.TwitterTable;
import org.springframework.data.cassandra.core.CassandraTemplate;

public class TwitterDBService extends DBService<TwitterTableRepo, TwitterFriendsTableRepo, TwitterPostsTableRepo,
        TwitterTable, TwitterPostsTable, String, TwitterFriendsTable> {

    public TwitterDBService(TwitterTableRepo twitterTableRepo,
                            TwitterFriendsTableRepo twitterFriendsTableRepo,
                            TwitterPostsTableRepo twitterPostsTableRepo,
                            CassandraTemplate cassandraTemplate,
                            RelatedTableRepo relatedTableRepo,
                            int dbTwitterFriendsThreads,
                            int dbTwitterPostsThreads) {
        super(
            twitterTableRepo,
            twitterFriendsTableRepo,
            twitterPostsTableRepo,
            cassandraTemplate,
            "twitter_unindexed",
            "twitter_need_friends",
            String.class,
            relatedTableRepo,
            TwitterDBService.class,
            dbTwitterFriendsThreads,
            dbTwitterPostsThreads
        );
    }

}

