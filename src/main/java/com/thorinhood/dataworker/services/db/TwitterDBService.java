package com.thorinhood.dataworker.services.db;

import com.thorinhood.dataworker.repositories.TwitterTableRepo;
import com.thorinhood.dataworker.tables.TwitterTable;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Service;

@Service
public class TwitterDBService extends DBService<TwitterTableRepo, TwitterTable, String> {

    public TwitterDBService(TwitterTableRepo twitterTableRepo,
                            CassandraTemplate cassandraTemplate) {
        super(
                twitterTableRepo,
                cassandraTemplate,
                "twitter_unindexed",
                "twitter_need_friends",
                String.class
        );
    }

}

