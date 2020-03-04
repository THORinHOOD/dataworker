package com.thorinhood.dataworker.services.db;

import com.thorinhood.dataworker.repositories.RelatedTableRepo;
import com.thorinhood.dataworker.repositories.VKTableRepo;
import com.thorinhood.dataworker.repositories.VKUnindexedTableRepo;
import com.thorinhood.dataworker.tables.VKTable;
import com.thorinhood.dataworker.tables.VKUnindexedTable;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Service;

@Service
public class VKDBService extends DBService<VKTableRepo, VKUnindexedTableRepo, VKTable, VKUnindexedTable, Long, String> {

    public VKDBService(VKTableRepo vkTableRepo,
                       VKUnindexedTableRepo vkUnindexedTableRepo,
                       CassandraTemplate cassandraTemplate,
                       RelatedTableRepo relatedTableRepo) {
        super(
                vkTableRepo,
                vkUnindexedTableRepo,
                cassandraTemplate,
                "vk_unindexed",
                "vk_need_friends",
                Long.class,
                String.class,
                relatedTableRepo
        );
    }

}
