package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.repositories.VKTableRepo;
import com.thorinhood.dataworker.tables.VKTable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class DBService {

    private final VKTableRepo vkTableRepo;

    public DBService(VKTableRepo vkTableRepo) {
        this.vkTableRepo = vkTableRepo;
    }

    public void save(Collection<VKTable> vkTable) {
        vkTableRepo.saveAll(vkTable);
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
