package com.thorinhood.dataworker.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monitoring")
public class HealthMonitoringController {

    public static final long MB = 1024 * 1024;
    private final Runtime runtime;

    public HealthMonitoringController() {
        runtime = Runtime.getRuntime();
    }

    @GetMapping("/memory/used")
    public long getUsedMemory() {
        return (runtime.totalMemory() - runtime.freeMemory()) / MB;
    }

    @GetMapping("/memory/free")
    public long getFreeMemory() {
        return (runtime.freeMemory()) / MB;
    }

    @GetMapping("/memory/total")
    public long getTotalMemory() {
        return (runtime.totalMemory()) / MB;
    }

    @GetMapping("/memory/max")
    public long getMaxMemory() {
        return runtime.maxMemory() / MB;
    }

}
