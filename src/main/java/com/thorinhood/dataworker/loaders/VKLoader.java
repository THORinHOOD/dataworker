package com.thorinhood.dataworker.loaders;

import com.thorinhood.dataworker.services.DBService;
import com.thorinhood.dataworker.services.VKService;
import com.thorinhood.dataworker.tables.VKTable;
import org.springframework.scheduling.annotation.Scheduled;

public class VKLoader extends CommonLoader<VKTable> {

    public VKLoader(DBService dbService, VKService vkService) {
        super(dbService, vkService);
    }

    @Scheduled(fixedRate = 5000)
    @Override
    void loadData() {
        System.out.println("HELLO");
    }

}
