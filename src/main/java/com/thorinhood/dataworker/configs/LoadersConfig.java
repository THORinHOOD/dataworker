package com.thorinhood.dataworker.configs;

import com.thorinhood.dataworker.loaders.Loader;
import com.thorinhood.dataworker.repositories.posts.VKPostsTableRepo;
import com.thorinhood.dataworker.services.TwitterService;
import com.thorinhood.dataworker.services.VKService;
import com.thorinhood.dataworker.db.TwitterDBService;
import com.thorinhood.dataworker.cache.TwitterProfilesCache;
import com.thorinhood.dataworker.db.VKDBService;
import com.thorinhood.dataworker.cache.VKProfilesCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class LoadersConfig {

    @Bean
    public Loader loader(VKService vkService,
                         TwitterService twitterService,
                         VKDBService vkdbService,
                         TwitterDBService twitterDBService,
                         VKProfilesCache vkProfilesCache,
                         TwitterProfilesCache twitterProfilesCache,
                         VKPostsTableRepo vkPostsTableRepo) {
        return new Loader
        (
            vkService,
            twitterService,
            vkdbService,
            twitterDBService,
            vkProfilesCache,
            twitterProfilesCache,
            vkPostsTableRepo
        );
    }

//    @Bean
//    public VKLoader vkLoader(VKDBService dbService,
//                             VKService vkService) {
//        return new VKLoader(dbService, vkService);
//    }

//    @Bean
//    public TwitterLoader twitterLoader(TwitterDBService dbService,
//                                       TwitterService twitterService) {
//        return new TwitterLoader(dbService, twitterService);
//    }


}
