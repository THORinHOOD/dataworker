package com.thorinhood.dataworker.services.common;

public class MemoryService {

    public static final long MB = 1024 * 1024;
    private final Runtime runtime;

    public MemoryService() {
        runtime = Runtime.getRuntime();
    }

    public long getUsedMemory() {
        return (runtime.totalMemory() - runtime.freeMemory()) / MB;
    }

    public long getFreeMemory() {
        return (runtime.freeMemory()) / MB;
    }

    public long getTotalMemory() {
        return (runtime.totalMemory()) / MB;
    }

    public long getMaxMemory() {
        return runtime.maxMemory() / MB;
    }

}
