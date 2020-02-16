package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.repositories.MyColumnsRepo;
import com.thorinhood.dataworker.repositories.VKTableRepo;
import com.thorinhood.dataworker.tables.MyColumns;
import com.thorinhood.dataworker.tables.VKTable;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class DBService {

    private final MyColumnsRepo myColumnsRepo;

    private final VKTableRepo vkTableRepo;

    public DBService(MyColumnsRepo myColumnsRepo,
                     VKTableRepo vkTableRepo) {
        this.myColumnsRepo = myColumnsRepo;
        this.vkTableRepo = vkTableRepo;
    }

    public void save(Collection<VKTable> vkTable) {
        vkTableRepo.saveAll(vkTable);
    }

    public void saveRow(String id, String name) {
        myColumnsRepo.save(new MyColumns(id, name));
    }


}
