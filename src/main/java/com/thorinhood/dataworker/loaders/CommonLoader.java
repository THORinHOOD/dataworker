package com.thorinhood.dataworker.loaders;

import com.thorinhood.dataworker.utils.common.SocialService;

public abstract class CommonLoader<DB, DATA> {

    protected final DB dbService;
    protected final SocialService<DATA> service;

    public CommonLoader(DB dbService, SocialService<DATA> service) {
        this.dbService = dbService;
        this.service = service;
    }

    abstract void loadData();

}
