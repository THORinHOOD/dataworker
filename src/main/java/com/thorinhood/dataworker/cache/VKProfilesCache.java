package com.thorinhood.dataworker.cache;

import com.thorinhood.dataworker.db.VKDBService;
import com.thorinhood.dataworker.repositories.profiles.VKTableRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class VKProfilesCache extends CacheService<String> {

    private static final Logger logger = LoggerFactory.getLogger(VKProfilesCache.class);
    private final VKTableRepo vkTableRepo;

    public VKProfilesCache(VKDBService vkdbService, VKTableRepo vkTableRepo, int max) {
        super(max, logger, "vk profiles");
        this.vkTableRepo = vkTableRepo;
        vkdbService.subscribeOnSave(this::handleSave);
    }

    @Override
    void onSaveEnd(Collection<String> ids) {
        // nothing
    }

    @Override
    protected boolean notFound(String id) {
        if (vkTableRepo.existsByDomain(id)) {
            add(id);
            return true;
        }
        return false;
    }

    @Override
    boolean additionalFilterCondition(String object) {
        return !object.equalsIgnoreCase("null");
    }

}
