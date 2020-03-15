package com.thorinhood.dataworker.configs;

import com.thorinhood.dataworker.repositories.RelatedTableRepo;
import com.thorinhood.dataworker.repositories.TwitterFriendsTableRepo;
import com.thorinhood.dataworker.repositories.TwitterTableRepo;
import com.thorinhood.dataworker.repositories.VKFriendsTableRepo;
import com.thorinhood.dataworker.repositories.VKTableRepo;
import com.thorinhood.dataworker.services.VKFriendsService;
import com.thorinhood.dataworker.services.VKService;
import com.thorinhood.dataworker.services.db.VKDBService;
import com.thorinhood.dataworker.services.cache.VKProfilesCache;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
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

    @Value("${vk.service.friends.threads}")
    private Integer vkFriendsServiceThreadsCount;

    @Value("${vk.db.service.friends.threads}")
    private Integer vkDBFriendsServiceThreadsCount;

    @Bean
    public VKService vkService(VKDBService dbService,
                               VKFriendsService vkFriendsService) throws ClientException, ApiException {
        return new VKService(serviceAccessKey, clientSecret, appId, dbService, vkFriendsService);
    }

    @Bean
    public VKFriendsService vkFriendsService() {
        return new VKFriendsService(vkFriendsServiceThreadsCount);
    }

    @Bean
    public VKProfilesCache vkProfilesCahce(VKDBService vkdbService) {
        return new VKProfilesCache(vkdbService);
    }

    @Bean
    public VKDBService vkdbService(VKTableRepo vkTableRepo,
                                   VKFriendsTableRepo vkFriendsTableRepo,
                                   CassandraTemplate cassandraTemplate,
                                   RelatedTableRepo relatedTableRepo,
                                   JdbcTemplate postgresJdbc) {
        return new VKDBService(
                vkTableRepo,
                vkFriendsTableRepo,
                cassandraTemplate,
                relatedTableRepo,
                postgresJdbc,
                vkDBFriendsServiceThreadsCount
        );
    }

}
