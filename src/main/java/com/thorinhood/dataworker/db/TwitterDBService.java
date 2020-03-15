package com.thorinhood.dataworker.db;

import com.thorinhood.dataworker.repositories.RelatedTableRepo;
import com.thorinhood.dataworker.repositories.TwitterFriendsTableRepo;
import com.thorinhood.dataworker.repositories.TwitterTableRepo;
import com.thorinhood.dataworker.tables.TwitterFriendsTable;
import com.thorinhood.dataworker.tables.TwitterTable;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

public class TwitterDBService extends DBService<TwitterTableRepo, TwitterFriendsTableRepo, TwitterTable, String,
        TwitterFriendsTable> {

    public TwitterDBService(TwitterTableRepo twitterTableRepo,
                            TwitterFriendsTableRepo twitterFriendsTableRepo,
                            CassandraTemplate cassandraTemplate,
                            RelatedTableRepo relatedTableRepo,
                            JdbcTemplate postgresJdbc,
                            int dbTwitterFriendsThreads) {
        super(
            twitterTableRepo,
            twitterFriendsTableRepo,
            cassandraTemplate,
            "twitter_unindexed",
            "twitter_need_friends",
            String.class,
            relatedTableRepo,
            postgresJdbc,
            TwitterDBService.class,
            dbTwitterFriendsThreads
        );
    }

}

