package com.thorinhood.dataworker.services.db;

import com.thorinhood.dataworker.repositories.VKTableRepo;
import com.thorinhood.dataworker.tables.VKTable;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Service;

@Service
public class VKDBService extends DBService<VKTableRepo, VKTable, Long> {

    public VKDBService(VKTableRepo vkTableRepo,
                       CassandraTemplate cassandraTemplate) {
        super(
                vkTableRepo,
                cassandraTemplate,
                "vk_unindexed",
                "vk_need_friends",
                Long.class
        );
    }

}
