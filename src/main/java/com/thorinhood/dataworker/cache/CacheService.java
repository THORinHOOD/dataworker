package com.thorinhood.dataworker.cache;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CacheService<T> {

    protected final HashSet<T> cache;
    protected final Logger logger;
    protected final String info;

    public CacheService(Logger logger, String info) {
        cache = new HashSet<>();
        this.logger = logger;
        this.info = info;
    }

    public void handleSave(Collection<T> objects) {
        logger.info(String.format("Start caching [%s]", info));
        onSave(objects);
        logger.info(String.format("End caching [%s]", info));
    }

    protected abstract void onSave(Collection<T> objects);
    abstract boolean contains(T object);
    abstract boolean additionalFilterCondition(T object);

    public Stream<List<T>> filter(Stream<List<T>> batches) {
        return batches
                .filter(CollectionUtils::isNotEmpty)
                .map(partition -> partition.stream()
                        .filter(Objects::nonNull)
                        .filter(this::additionalFilterCondition)
                        .filter(x -> !contains(x))
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty);
    }

}