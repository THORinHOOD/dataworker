package com.thorinhood.dataworker.services.db;

import com.thorinhood.dataworker.repositories.TwitterTableRepo;
import com.thorinhood.dataworker.repositories.TwitterUnindexedTableRepo;
import com.thorinhood.dataworker.tables.TwitterTable;
import com.thorinhood.dataworker.tables.TwitterUnindexedTable;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Service;

@Service
public class TwitterDBService extends DBService<TwitterTableRepo, TwitterUnindexedTableRepo, TwitterTable,
        TwitterUnindexedTable, String, String> {

    public TwitterDBService(TwitterTableRepo twitterTableRepo,
                            TwitterUnindexedTableRepo twitterUnindexedTableRepo,
                            CassandraTemplate cassandraTemplate) {
        super(
                twitterTableRepo,
                twitterUnindexedTableRepo,
                cassandraTemplate,
                "twitter_unindexed",
                "twitter_need_friends",
                String.class,
                String.class
        );
    }

}

