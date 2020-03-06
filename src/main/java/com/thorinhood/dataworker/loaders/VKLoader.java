package com.thorinhood.dataworker.loaders;

import com.thorinhood.dataworker.repositories.RelatedTableRepo;
import com.thorinhood.dataworker.repositories.VKTableRepo;
import com.thorinhood.dataworker.repositories.VKUnindexedTableRepo;
import com.thorinhood.dataworker.services.VKService;
import com.thorinhood.dataworker.services.db.VKDBService;
import com.thorinhood.dataworker.tables.VKTable;
import com.thorinhood.dataworker.tables.VKUnindexedTable;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;

public class VKLoader extends CommonLoader<VKDBService, VKTableRepo, VKUnindexedTableRepo, VKTable, VKUnindexedTable, Long, String> {

    private RelatedTableRepo relatedTableRepo;

    public VKLoader(VKDBService dbService, VKService vkService, RelatedTableRepo relatedTableRepo) {
        super(dbService, vkService);
        this.relatedTableRepo = relatedTableRepo;
    }

    @Scheduled(fixedRate = 10000000)
    public void loadData() {
        super.loadData(Arrays.asList(
            172252308L
        ));
    }

}
