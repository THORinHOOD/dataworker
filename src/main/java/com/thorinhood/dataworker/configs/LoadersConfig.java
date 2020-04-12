package com.thorinhood.dataworker.configs;

import com.thorinhood.dataworker.jobs.TwitterUpdater;
import com.thorinhood.dataworker.jobs.VkUpdater;
import com.thorinhood.dataworker.loaders.Loader;
import com.thorinhood.dataworker.repositories.TwitterReposBundle;
import com.thorinhood.dataworker.repositories.VkReposBundle;
import com.thorinhood.dataworker.repositories.posts.VKPostsTableRepo;
import com.thorinhood.dataworker.repositories.related.RelatedTableRepo;
import com.thorinhood.dataworker.services.social.TwitterService;
import com.thorinhood.dataworker.services.social.VKService;
import com.thorinhood.dataworker.db.TwitterDBService;
import com.thorinhood.dataworker.cache.TwitterProfilesCache;
import com.thorinhood.dataworker.db.VKDBService;
import com.thorinhood.dataworker.cache.VKProfilesCache;
import com.thorinhood.dataworker.services.unite.SimilarityService;
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

    @Bean
    public VkUpdater vkUpdater(SimilarityService similarityService,
                               RelatedTableRepo relatedTableRepo,
                               VkReposBundle vkReposBundle,
                               TwitterReposBundle twitterReposBundle) {
        return new VkUpdater(similarityService, relatedTableRepo, vkReposBundle, twitterReposBundle);
    }

    @Bean
    public TwitterUpdater twitterUpdater(SimilarityService similarityService,
                                         RelatedTableRepo relatedTableRepo,
                                         TwitterReposBundle twitterReposBundle) {
        return new TwitterUpdater(similarityService, relatedTableRepo, twitterReposBundle);
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
