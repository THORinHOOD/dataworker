package com.thorinhood.dataworker.services.db;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ProfilesCache {

    protected final HashSet<String> cache;

    public ProfilesCache() {
        cache = new HashSet<>();
    }

    abstract void onSave(Collection<String> ids);
    abstract boolean contains(String id);

    public Stream<List<String>> filter(Stream<List<String>> ids) {
        return ids
            .filter(CollectionUtils::isNotEmpty)
            .map(partition -> partition.stream()
                .filter(Objects::nonNull)
                .filter(id -> !id.equalsIgnoreCase("null"))
                .filter(id -> !contains(id))
                .collect(Collectors.toList()))
            .filter(CollectionUtils::isNotEmpty);
    }

}
