package com.thorinhood.dataworker.cache;

import com.thorinhood.dataworker.db.VKDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class VKProfilesCache extends CacheService<String> {

    private static final Logger logger = LoggerFactory.getLogger(VKProfilesCache.class);

    public VKProfilesCache(VKDBService vkdbService) {
        super(logger, "vk profiles");
        vkdbService.subscribeOnSave(this::handleSave);
    }

    @Override
    public void onSave(Collection<String> ids) {
        cache.addAll(ids);
    }

    @Override
    public boolean contains(String id) {
        return cache.contains(id);
    }

    @Override
    boolean additionalFilterCondition(String object) {
        return !object.equalsIgnoreCase("null");
    }

}
