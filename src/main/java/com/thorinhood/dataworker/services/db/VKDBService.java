package com.thorinhood.dataworker.services.db;

import com.thorinhood.dataworker.repositories.VKTableRepo;
import com.thorinhood.dataworker.tables.VKTable;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VKDBService extends DBService<VKTableRepo, VKTable, Long> {

    public VKDBService(VKTableRepo vkTableRepo,
                       CassandraTemplate cassandraTemplate) {
        super(vkTableRepo, cassandraTemplate, "vk_unindexed", "vk_need_friends");
    }

    @Override
    public List<Long> getAllUnindexedPages() {
        return getAllUnindexedPages(Long.class);
    }

}
