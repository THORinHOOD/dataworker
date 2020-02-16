package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.repositories.VKTableRepo;
import com.thorinhood.dataworker.tables.VKTable;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class DBService {

    private final VKTableRepo vkTableRepo;

    public DBService(VKTableRepo vkTableRepo) {
        this.vkTableRepo = vkTableRepo;
    }

    public void save(Collection<VKTable> vkTable) {
        vkTableRepo.saveAll(vkTable);
    }

}
