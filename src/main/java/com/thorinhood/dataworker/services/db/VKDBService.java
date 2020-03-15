package com.thorinhood.dataworker.services.db;

import com.thorinhood.dataworker.repositories.RelatedTableRepo;
import com.thorinhood.dataworker.repositories.VKFriendsTableRepo;
import com.thorinhood.dataworker.repositories.VKTableRepo;
import com.thorinhood.dataworker.tables.VKFriendsTable;
import com.thorinhood.dataworker.tables.VKTable;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

public class VKDBService extends DBService<VKTableRepo, VKFriendsTableRepo, VKTable, String, VKFriendsTable> {

    public VKDBService(VKTableRepo vkTableRepo,
                       VKFriendsTableRepo vkFriendsTableRepo,
                       CassandraTemplate cassandraTemplate,
                       RelatedTableRepo relatedTableRepo,
                       JdbcTemplate postgresJdbc,
                       int dbServiceFriendsThreads) {
        super(
            vkTableRepo,
            vkFriendsTableRepo,
            cassandraTemplate,
            "work.vk_unindexed",
            "vk_need_friends",
            String.class,
            relatedTableRepo,
            postgresJdbc,
            VKDBService.class,
            dbServiceFriendsThreads
        );
    }

}
