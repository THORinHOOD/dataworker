package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.repositories.VKIndexTableRepo;
import com.thorinhood.dataworker.repositories.VKTableRepo;
import com.thorinhood.dataworker.tables.VKTable;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class VKDBService {

    private final VKTableRepo vkTableRepo;
    private final VKIndexTableRepo vkIndexTableRepo;
    private final CassandraTemplate cassandraTemplate;

    public VKDBService(VKTableRepo vkTableRepo,
                       VKIndexTableRepo vkIndexTableRepo,
                       CassandraTemplate cassandraTemplate) {
        this.vkTableRepo = vkTableRepo;
        this.vkIndexTableRepo = vkIndexTableRepo;
        this.cassandraTemplate = cassandraTemplate;
    }

    public List<Integer> getAllUnindexedPages() {
        return cassandraTemplate.getCqlOperations().queryForList("SELECT id FROM vk_unindexed", Integer.class);
    }

    public void savePages(Collection<VKTable> vkTables) {
        vkTableRepo.saveAll(vkTables);
        vkTables.forEach(vkTable -> {
            cassandraTemplate.getCqlOperations().execute("DELETE FROM vk_unindexed WHERE id = ?", vkTable.getId());
            cassandraTemplate.getCqlOperations().execute("INSERT INTO vk_need_friends (id) VALUES (?)",
                    vkTable.getId());
        });
    }

    public Optional<VKTable> getPageById(String id) {
        Long idVk;
        try {
            idVk = Long.valueOf(id);
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
        return vkTableRepo.findById(idVk);
    }

    public boolean containsVKPage(Integer id) {
        return vkTableRepo.existsById(Long.valueOf(id));
    }

}
