package com.thorinhood.dataworker.configs;

import com.thorinhood.dataworker.services.common.MemoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class MonitoringConfig {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringConfig.class);
    private static final String MEMORY_INFO = "\nMemory info : \n\tTotal : %d mb\n\tMax : %d mb\n\tUsed : %d mb\n\tFree : %d mb";
    private final MemoryService memoryService;

    public MonitoringConfig() {
        memoryService = new MemoryService();
    }

    @Bean
    public MemoryService memoryService() {
        return memoryService;
    }

    @Scheduled(fixedRate = 60000L)
    public void monitoringMemory() {
        logger.info(String.format(MEMORY_INFO, memoryService.getTotalMemory(), memoryService.getMaxMemory(),
                memoryService.getUsedMemory(), memoryService.getFreeMemory()));
    }

}
