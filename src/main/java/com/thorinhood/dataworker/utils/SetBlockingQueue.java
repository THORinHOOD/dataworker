package com.thorinhood.dataworker.utils;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class SetBlockingQueue<T> extends LinkedBlockingQueue<T> {

    private Set<T> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public synchronized boolean add(T t) {
        if (set.contains(t)) {
            return false;
        } else {
            set.add(t);
            return super.add(t);
        }
    }

    @Override
    public T take() throws InterruptedException {
        T t = super.take();
        set.remove(t);
        return t;
    }

}
