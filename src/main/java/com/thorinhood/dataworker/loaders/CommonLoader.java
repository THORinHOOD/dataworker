package com.thorinhood.dataworker.loaders;

import com.thorinhood.dataworker.services.DBService;
import com.thorinhood.dataworker.utils.common.SocialService;

public abstract class CommonLoader<DATA> {

    protected final DBService dbService;
    protected final SocialService<DATA> service;

    public CommonLoader(DBService dbService, SocialService<DATA> service) {
        this.dbService = dbService;
        this.service = service;
    }

    abstract void loadData();

}
