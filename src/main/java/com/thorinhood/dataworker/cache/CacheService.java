package com.thorinhood.dataworker.cache;

import com.thorinhood.dataworker.utils.MeasureTimeUtil;
import com.thorinhood.dataworker.utils.SetBlockingQueue;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CacheService<T> {

    protected final SetBlockingQueue<T> cache;
    protected final Logger logger;
    protected final String info;
    protected final int max;
    private final MeasureTimeUtil measureTimeUtil;

    public CacheService(int max, Logger logger, String info) {
        this.logger = logger;
        this.info = info;
        cache = new SetBlockingQueue<>();
        this.max = max;
        measureTimeUtil = new MeasureTimeUtil();
    }

    public synchronized void handleSave(Collection<T> objects) {
        measureTimeUtil.measure(data -> {
            data.forEach(this::add);
            onSaveEnd(data);
        }, objects, logger, info + " caching", objects.size());
    }

    protected synchronized void add(T object) {
        if (cache.add(object)) {
            while (cache.size() >= max) {
                try {
                    cache.take();
                } catch (InterruptedException e) {
                    logger.error(String.format("Can't remove element from cache [%s]", info));
                }
            }
        }
    }

    public synchronized boolean contains(T object) {
        return cache.contains(object) || notFound(object);
    }

    abstract void onSaveEnd(Collection<T> objects);
    protected abstract boolean notFound(T object);
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
