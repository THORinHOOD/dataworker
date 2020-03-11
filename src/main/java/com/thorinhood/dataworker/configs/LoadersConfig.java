package com.thorinhood.dataworker.configs;

import com.thorinhood.dataworker.loaders.VKLoader;
import com.thorinhood.dataworker.services.VKService;
import com.thorinhood.dataworker.services.db.VKDBService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class LoadersConfig {

    @Bean
    public VKLoader vkLoader(VKDBService dbService,
                             VKService vkService) {
        return new VKLoader(dbService, vkService);
    }

}
