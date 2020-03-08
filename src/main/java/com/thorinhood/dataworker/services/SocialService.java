package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.tables.Profile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;

public abstract class SocialService<TABLE extends Profile<ID>, ID> {

    protected final Logger logger;

    protected SocialService(Class serviceClass) {
        logger = LogManager.getLogger(serviceClass);
    }

    public abstract Collection<TABLE> getUsersInfo(List<ID> ids);

}
