package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.tables.Profile;
import com.thorinhood.dataworker.utils.common.BatchProfiles;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;

public abstract class SocialService<TABLE extends Profile<ID>, ID> {

    protected final Logger logger;

    protected SocialService(Class serviceClass) {
        logger = LogManager.getLogger(serviceClass);
    }

    public void getUsersInfo(Collection<ID> ids, BlockingQueue<BatchProfiles<TABLE, ID>> queue) {
        logger.info("Starting to load profiles : " + ids.size());
        getDefaultUsersInfo(ids, queue);
        logger.info("Ended loading profiles : " + ids.size());
    }

    protected abstract void getDefaultUsersInfo(Collection<ID> ids, BlockingQueue<BatchProfiles<TABLE, ID>> queue);

}
