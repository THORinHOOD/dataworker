package com.thorinhood.dataworker.cache;

import com.thorinhood.dataworker.db.TwitterDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class TwitterProfilesCache extends CacheService<String> {

    private static final Logger logger = LoggerFactory.getLogger(TwitterProfilesCache.class);
    private Collection<Consumer<Collection<String>>> onSaveHandlers;


    public TwitterProfilesCache(TwitterDBService twitterDBService) {
        super(logger, "twitter profiles");
        onSaveHandlers = new ArrayList<>();
        twitterDBService.subscribeOnSave(this::handleSave);
    }

    @Override
    public void onSave(Collection<String> ids) {
        cache.addAll(ids);
        onSaveHandlers.forEach(x -> x.accept(ids));
    }

    @Override
    public boolean contains(String id) {
        return cache.contains(id);
    }

    @Override
    boolean additionalFilterCondition(String object) {
        return !object.equalsIgnoreCase("null");
    }

    public void subscribeOnSave(Consumer<Collection<String>> onSaveHandler) {
        onSaveHandlers.add(onSaveHandler);
    }

}
