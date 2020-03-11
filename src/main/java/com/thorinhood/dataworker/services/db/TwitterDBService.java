package com.thorinhood.dataworker.services.db;

import com.thorinhood.dataworker.repositories.RelatedTableRepo;
import com.thorinhood.dataworker.repositories.TwitterFriendsTableRepo;
import com.thorinhood.dataworker.repositories.TwitterTableRepo;
import com.thorinhood.dataworker.tables.TwitterFriendsPrimaryKey;
import com.thorinhood.dataworker.tables.TwitterFriendsTable;
import com.thorinhood.dataworker.tables.TwitterTable;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class TwitterDBService extends DBService<TwitterTableRepo, TwitterFriendsTableRepo, TwitterTable, String,
        TwitterFriendsTable, TwitterFriendsPrimaryKey> {

    public TwitterDBService(TwitterTableRepo twitterTableRepo,
                            TwitterFriendsTableRepo twitterFriendsTableRepo,
                            CassandraTemplate cassandraTemplate,
                            RelatedTableRepo relatedTableRepo,
                            JdbcTemplate postgresJdbc) {
        super(
            twitterTableRepo,
            twitterFriendsTableRepo,
            cassandraTemplate,
            "twitter_unindexed",
            "twitter_need_friends",
            String.class,
            relatedTableRepo,
            postgresJdbc,
            TwitterDBService.class
        );
    }

}

