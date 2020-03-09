package com.thorinhood.dataworker.loaders;

import com.thorinhood.dataworker.repositories.RelatedTableRepo;
import com.thorinhood.dataworker.repositories.VKTableRepo;
import com.thorinhood.dataworker.repositories.VKUnindexedTableRepo;
import com.thorinhood.dataworker.services.VKFriendsService;
import com.thorinhood.dataworker.services.VKService;
import com.thorinhood.dataworker.services.db.VKDBService;
import com.thorinhood.dataworker.tables.VKTable;
import com.thorinhood.dataworker.tables.VKUnindexedTable;
import org.json.simple.parser.ParseException;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VKLoader extends CommonLoader<VKDBService, VKTableRepo, VKUnindexedTableRepo, VKTable, VKUnindexedTable,
        String> {

    private RelatedTableRepo relatedTableRepo;

    public VKLoader(VKDBService dbService, VKService vkService, RelatedTableRepo relatedTableRepo) {
        super(dbService, vkService, VKLoader.class);
        this.relatedTableRepo = relatedTableRepo;
    }

    @PostConstruct
    public void loadData() {
        List<String> next = Collections.singletonList("thorinhoodie");
        int depth = 10;
        while (depth > 0) {
            List<String> buffer = super.loadData(next);
            next.clear();
            next = buffer;
            depth--;
        }
    }

}
