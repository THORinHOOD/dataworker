package com.thorinhood.dataworker.controllers;

import com.thorinhood.dataworker.services.common.MemoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {

    private final MemoryService memoryService;

    public MonitoringController(MemoryService memoryService) {
        this.memoryService = memoryService;
    }

    @GetMapping("/memory/used")
    public long getUsedMemory() {
        return memoryService.getUsedMemory();
    }

    @GetMapping("/memory/free")
    public long getFreeMemory() {
        return memoryService.getFreeMemory();
    }

    @GetMapping("/memory/total")
    public long getTotalMemory() {
        return memoryService.getTotalMemory();
    }

    @GetMapping("/memory/max")
    public long getMaxMemory() {
        return memoryService.getMaxMemory();
    }

}
