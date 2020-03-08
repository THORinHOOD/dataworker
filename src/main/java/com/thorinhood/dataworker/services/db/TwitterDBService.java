package com.thorinhood.dataworker.services.db;

import com.thorinhood.dataworker.repositories.RelatedTableRepo;
import com.thorinhood.dataworker.repositories.TwitterTableRepo;
import com.thorinhood.dataworker.repositories.TwitterUnindexedTableRepo;
import com.thorinhood.dataworker.tables.TwitterTable;
import com.thorinhood.dataworker.tables.TwitterUnindexedTable;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class TwitterDBService extends DBService<TwitterTableRepo, TwitterUnindexedTableRepo, TwitterTable,
        TwitterUnindexedTable, String> {

    public TwitterDBService(TwitterTableRepo twitterTableRepo,
                            TwitterUnindexedTableRepo twitterUnindexedTableRepo,
                            CassandraTemplate cassandraTemplate,
                            RelatedTableRepo relatedTableRepo,
                            JdbcTemplate postgresJdbc) {
        super(
            twitterTableRepo,
            twitterUnindexedTableRepo,
            cassandraTemplate,
            "twitter_unindexed",
            "twitter_need_friends",
            String.class,
            relatedTableRepo,
            TwitterUnindexedTable::new,
            postgresJdbc,
            TwitterDBService.class
        );
    }

}

