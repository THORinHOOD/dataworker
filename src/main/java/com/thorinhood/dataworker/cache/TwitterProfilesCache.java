package com.thorinhood.dataworker.cache;

import com.thorinhood.dataworker.db.TwitterDBService;
import com.thorinhood.dataworker.repositories.profiles.TwitterTableRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class TwitterProfilesCache extends CacheService<String> {

    private static final Logger logger = LoggerFactory.getLogger(TwitterProfilesCache.class);
    private Collection<Consumer<Collection<String>>> onSaveHandlers;
    private final TwitterTableRepo twitterTableRepo;

    public TwitterProfilesCache(TwitterDBService twitterDBService, TwitterTableRepo twitterTableRepo, int max) {
        super(max, logger, "twitter profiles");
        onSaveHandlers = new ArrayList<>();
        this.twitterTableRepo = twitterTableRepo;
        twitterDBService.subscribeOnSave(this::handleSave);
    }

    @Override
    void onSaveEnd(Collection<String> ids) {
        onSaveHandlers.forEach(x -> x.accept(ids));
    }

    @Override
    protected boolean notFound(String object) {
        if (twitterTableRepo.existsById(object)) {
            add(object);
            return true;
        }
        return false;
    }

    @Override
    boolean additionalFilterCondition(String object) {
        return !object.equalsIgnoreCase("null");
    }

    public void subscribeOnSave(Consumer<Collection<String>> onSaveHandler) {
        onSaveHandlers.add(onSaveHandler);
    }

}
