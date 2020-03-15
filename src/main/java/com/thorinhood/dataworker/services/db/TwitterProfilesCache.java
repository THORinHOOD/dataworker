package com.thorinhood.dataworker.services.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class TwitterProfilesCache extends ProfilesCache {

    private static final Logger logger = LoggerFactory.getLogger(TwitterProfilesCache.class);

    public TwitterProfilesCache(TwitterDBService twitterDBService) {
        twitterDBService.subscribeOnSave(this::onSave);
    }

    @Override
    public void onSave(Collection<String> ids) {
        logger.info("Saving twitter profiles to cache...");
        cache.addAll(ids);
        logger.info("Saved twitter profiles to cache...");
    }

    @Override
    public boolean contains(String id) {
        return cache.contains(id);
    }

}
