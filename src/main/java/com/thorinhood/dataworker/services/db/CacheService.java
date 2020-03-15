package com.thorinhood.dataworker.services.db;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CacheService<T> {

    protected final HashSet<T> cache;

    public CacheService() {
        cache = new HashSet<>();
    }

    abstract void onSave(Collection<T> objects);
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