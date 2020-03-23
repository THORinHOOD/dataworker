package com.thorinhood.dataworker.db;

import com.thorinhood.dataworker.repositories.posts.VKPostsTableRepo;
import com.thorinhood.dataworker.repositories.related.RelatedTableRepo;
import com.thorinhood.dataworker.repositories.friends.VKFriendsTableRepo;
import com.thorinhood.dataworker.repositories.profiles.VKTableRepo;
import com.thorinhood.dataworker.tables.friends.VKFriendsTable;
import com.thorinhood.dataworker.tables.posts.VKPostsTable;
import com.thorinhood.dataworker.tables.profile.VKTable;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

public class VKDBService extends DBService<VKTableRepo, VKFriendsTableRepo, VKPostsTableRepo, VKTable, VKPostsTable,
        String, VKFriendsTable> {

    public VKDBService(VKTableRepo vkTableRepo,
                       VKFriendsTableRepo vkFriendsTableRepo,
                       VKPostsTableRepo vkPostsTableRepo,
                       CassandraTemplate cassandraTemplate,
                       RelatedTableRepo relatedTableRepo,
                       JdbcTemplate postgresJdbc,
                       int dbServiceFriendsThreads,
                       int dbServicePostsThreads) {
        super(
            vkTableRepo,
            vkFriendsTableRepo,
            vkPostsTableRepo,
            cassandraTemplate,
            "work.vk_unindexed",
            "vk_need_friends",
            String.class,
            relatedTableRepo,
            postgresJdbc,
            VKDBService.class,
            dbServiceFriendsThreads,
            dbServicePostsThreads
        );
    }

}
