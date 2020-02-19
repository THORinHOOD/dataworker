package com.thorinhood.dataworker.loaders;

import com.thorinhood.dataworker.services.VKDBService;
import com.thorinhood.dataworker.services.VKService;
import com.thorinhood.dataworker.tables.VKTable;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class VKLoader extends CommonLoader<VKDBService, VKTable> {

    public VKLoader(VKDBService dbService, VKService vkService) {
        super(dbService, vkService);
    }

    @Scheduled(fixedRate = 5000)
    @Override
    void loadData() {
        List<Integer> unindexedPages = dbService.getAllUnindexedPages(); // TODO GET BATCH
        Collection<VKTable> users = service.getDefaultUsersInfo(unindexedPages.stream()
                .map(String::valueOf)
                .collect(Collectors.toList()));
        dbService.savePages(users);


    }

}
