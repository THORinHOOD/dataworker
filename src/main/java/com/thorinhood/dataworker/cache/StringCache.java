package com.thorinhood.dataworker.cache;

import org.slf4j.LoggerFactory;

import java.util.Collection;

public class StringCache extends CacheService<String> {

    public StringCache(String info) {
        super(LoggerFactory.getLogger(info), info);
    }

    @Override
    protected void onSave(Collection<String> objects) {
        cache.addAll(objects);
    }

    @Override
    boolean contains(String object) {
        return cache.contains(object);
    }

    @Override
    boolean additionalFilterCondition(String object) {
        return !"null".equalsIgnoreCase(object);
    }

}
