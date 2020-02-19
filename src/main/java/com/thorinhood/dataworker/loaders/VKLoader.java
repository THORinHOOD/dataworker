package com.thorinhood.dataworker.loaders;

import com.thorinhood.dataworker.services.VKDBService;
import com.thorinhood.dataworker.services.VKService;
import com.thorinhood.dataworker.tables.VKTable;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

public class VKLoader extends CommonLoader<VKDBService, VKTable> {

    private final CassandraTemplate cassandraTemplate;

    public VKLoader(VKDBService dbService,
                    VKService vkService,
                    CassandraTemplate cassandraTemplate) {
        super(dbService, vkService);
        this.cassandraTemplate = cassandraTemplate;
    }

    @Scheduled(fixedRate = 5000)
    @Override
    void loadData() {
        List<Integer> ids = new ArrayList<>();
        cassandraTemplate.getCqlOperations().query("SELECT id FROM vk_indexed WHERE indexed = false", rs -> {
            ids.add(rs.getInt("id"));
        });
        System.out.println(ids);
    }

}
