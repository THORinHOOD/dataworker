package com.thorinhood.dataworker.loaders;

import com.thorinhood.dataworker.repositories.RelatedTableRepo;
import com.thorinhood.dataworker.repositories.VKTableRepo;
import com.thorinhood.dataworker.repositories.VKUnindexedTableRepo;
import com.thorinhood.dataworker.services.VKService;
import com.thorinhood.dataworker.services.db.VKDBService;
import com.thorinhood.dataworker.tables.VKTable;
import com.thorinhood.dataworker.tables.VKUnindexedTable;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;

public class VKLoader extends CommonLoader<VKDBService, VKTableRepo, VKUnindexedTableRepo, VKTable, VKUnindexedTable,
        String> {

    private RelatedTableRepo relatedTableRepo;

    public VKLoader(VKDBService dbService, VKService vkService, RelatedTableRepo relatedTableRepo) {
        super(dbService, vkService, VKLoader.class);
        this.relatedTableRepo = relatedTableRepo;
    }

    @Scheduled(fixedRate = 1 * 60 * 1000)
    public void loadData() {
        super.loadData(dbService.takeUnindexedPages(100));
    }

}
