package com.thorinhood.dataworker.configs;

import com.thorinhood.dataworker.services.VKService;
import com.thorinhood.dataworker.services.db.VKDBService;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class VKConfiguration {

    @Value("${vk.service.access.key}")
    private String serviceAccessKey;

    @Value("${vk.client.secret}")
    private String clientSecret;

    @Value("${vk.app.id}")
    private Integer appId;

    @Bean
    public VKService vkService(VKDBService dbService) throws ClientException, ApiException {
        return new VKService(serviceAccessKey, clientSecret, appId, dbService);
    }

}
