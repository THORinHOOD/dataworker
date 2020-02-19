package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.repositories.TwitterTableRepo;
import com.thorinhood.dataworker.repositories.VKIndexTableRepo;
import com.thorinhood.dataworker.repositories.VKTableRepo;
import com.thorinhood.dataworker.tables.TwitterTable;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class DBService {

    private final VKTableRepo vkTableRepo;
    private final TwitterTableRepo twitterTableRepo;
    private final CassandraTemplate cassandraTemplate;
    private final VKIndexTableRepo vkIndexTableRepo;

    public DBService(VKTableRepo vkTableRepo,
                     TwitterTableRepo twitterTableRepo,
                     VKIndexTableRepo vkIndexTableRepo,
                     CassandraTemplate cassandraTemplate) {
        this.vkTableRepo = vkTableRepo;
        this.cassandraTemplate = cassandraTemplate;
        this.twitterTableRepo = twitterTableRepo;
        this.vkIndexTableRepo = vkIndexTableRepo;
    }

    public void saveTwitter(Collection<TwitterTable> twitterTables) {
        twitterTableRepo.saveAll(twitterTables);
    }


}
