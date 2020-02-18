package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.repositories.TwitterTableRepo;
import com.thorinhood.dataworker.repositories.VKTableRepo;
import com.thorinhood.dataworker.tables.TwitterTable;
import com.thorinhood.dataworker.tables.VKTable;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class DBService {

    private final VKTableRepo vkTableRepo;
    private final TwitterTableRepo twitterTableRepo;
    private final CassandraTemplate cassandraTemplate;

    public DBService(VKTableRepo vkTableRepo,
                     TwitterTableRepo twitterTableRepo,
                     CassandraTemplate cassandraTemplate) {
        this.vkTableRepo = vkTableRepo;
        this.cassandraTemplate = cassandraTemplate;
        this.twitterTableRepo = twitterTableRepo;
    }

    public void saveVK(Collection<VKTable> vkTable) {
        vkTableRepo.saveAll(vkTable);
    }

    public void saveTwitter(Collection<TwitterTable> twitterTables) {
        twitterTableRepo.saveAll(twitterTables);
    }

    public Optional<VKTable> getVKById(String id) {
        Long idVk;
        try {
            idVk = Long.valueOf(id);
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
        return vkTableRepo.findById(idVk);
    }

    public boolean containsVkUser(Integer id) {
        return vkTableRepo.existsById(Long.valueOf(id));
    }

}
