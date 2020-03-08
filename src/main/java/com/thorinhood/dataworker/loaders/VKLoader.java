package com.thorinhood.dataworker.loaders;

import com.thorinhood.dataworker.repositories.RelatedTableRepo;
import com.thorinhood.dataworker.repositories.VKTableRepo;
import com.thorinhood.dataworker.repositories.VKUnindexedTableRepo;
import com.thorinhood.dataworker.services.VKService;
import com.thorinhood.dataworker.services.db.VKDBService;
import com.thorinhood.dataworker.tables.VKTable;
import com.thorinhood.dataworker.tables.VKUnindexedTable;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;

public class VKLoader extends CommonLoader<VKDBService, VKTableRepo, VKUnindexedTableRepo, VKTable, VKUnindexedTable, Long, String> {

    private RelatedTableRepo relatedTableRepo;

    public VKLoader(VKDBService dbService, VKService vkService, RelatedTableRepo relatedTableRepo) {
        super(dbService, vkService, VKLoader.class);
        this.relatedTableRepo = relatedTableRepo;
    }

    @PostConstruct
    public void loadData() {
        new Thread(() -> super.loadData(Arrays.asList(
            172252308L
        ))).start();
    }

}
