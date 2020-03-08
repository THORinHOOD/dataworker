package com.thorinhood.dataworker.services.db;

import com.thorinhood.dataworker.repositories.RelatedTableRepo;
import com.thorinhood.dataworker.repositories.VKTableRepo;
import com.thorinhood.dataworker.repositories.VKUnindexedTableRepo;
import com.thorinhood.dataworker.tables.VKTable;
import com.thorinhood.dataworker.tables.VKUnindexedTable;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class VKDBService extends DBService<VKTableRepo, VKUnindexedTableRepo, VKTable, VKUnindexedTable, String> {

    public VKDBService(VKTableRepo vkTableRepo,
                       VKUnindexedTableRepo vkUnindexedTableRepo,
                       CassandraTemplate cassandraTemplate,
                       RelatedTableRepo relatedTableRepo,
                       JdbcTemplate postgresJdbc) {
        super(
                vkTableRepo,
                vkUnindexedTableRepo,
                cassandraTemplate,
                "work.vk_unindexed",
                "vk_need_friends",
                String.class,
                relatedTableRepo,
                VKUnindexedTable::new,
                postgresJdbc,
                VKDBService.class
        );
    }

}
