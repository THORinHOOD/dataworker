package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.repositories.VKIndexTableRepo;
import com.thorinhood.dataworker.repositories.VKTableRepo;
import com.thorinhood.dataworker.tables.VKIndexTable;
import com.thorinhood.dataworker.tables.VKTable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class VKDBService {

    private final VKTableRepo vkTableRepo;
    private final VKIndexTableRepo vkIndexTableRepo;

    public VKDBService(VKTableRepo vkTableRepo, VKIndexTableRepo vkIndexTableRepo) {
        this.vkTableRepo = vkTableRepo;
        this.vkIndexTableRepo = vkIndexTableRepo;
    }

    public List<VKIndexTable> getAllIndicies() {
        return vkIndexTableRepo.findAll();
    }

    public List<Integer> getAllUnindexedPages() {
        return vkIndexTableRepo.getUnindexed();
    }

    public void savePage(Collection<VKTable> vkTable) {
        vkTableRepo.saveAll(vkTable);
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
