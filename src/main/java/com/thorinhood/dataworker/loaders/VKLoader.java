package com.thorinhood.dataworker.loaders;

import com.thorinhood.dataworker.repositories.VKTableRepo;
import com.thorinhood.dataworker.services.VKService;
import com.thorinhood.dataworker.services.db.VKDBService;
import com.thorinhood.dataworker.tables.VKTable;
import org.springframework.scheduling.annotation.Scheduled;

public class VKLoader extends CommonLoader<VKDBService, VKTableRepo, VKTable, Long> {

    public VKLoader(VKDBService dbService, VKService vkService) {
        super(dbService, vkService);
    }

    @Scheduled(fixedRate = 10000000)
    @Override
    public void loadData() {
        super.loadData();
    }

}
