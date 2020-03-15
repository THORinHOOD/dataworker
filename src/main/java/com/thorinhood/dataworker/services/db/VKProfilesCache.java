package com.thorinhood.dataworker.services.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class VKProfilesCache extends ProfilesCache {

    private static final Logger logger = LoggerFactory.getLogger(VKProfilesCache.class);

    public VKProfilesCache(VKDBService vkdbService) {
       vkdbService.subscribeOnSave(this::onSave);
    }

    @Override
    public void onSave(Collection<String> ids) {
        logger.info("Saving vk profiles to cache...");
        cache.addAll(ids);
        logger.info("Saved vk profiles to cache...");
    }

    @Override
    public boolean contains(String id) {
        return cache.contains(id);
    }

}
