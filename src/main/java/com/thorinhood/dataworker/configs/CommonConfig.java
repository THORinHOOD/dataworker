package com.thorinhood.dataworker.configs;

import com.thorinhood.dataworker.repositories.TwitterReposBundle;
import com.thorinhood.dataworker.repositories.VkReposBundle;
import com.thorinhood.dataworker.repositories.friends.TwitterFriendsTableRepo;
import com.thorinhood.dataworker.repositories.friends.VKFriendsTableRepo;
import com.thorinhood.dataworker.repositories.posts.TwitterPostsTableRepo;
import com.thorinhood.dataworker.repositories.posts.VKPostsTableRepo;
import com.thorinhood.dataworker.repositories.profiles.TwitterTableRepo;
import com.thorinhood.dataworker.repositories.profiles.VKTableRepo;
import com.thorinhood.dataworker.repositories.related.RelatedTableRepo;
import com.thorinhood.dataworker.services.unite.SimilarityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    @Bean
    public SimilarityService similarityService(VkReposBundle vkReposBundle,
                                               TwitterReposBundle twitterReposBundle,
                                               RelatedTableRepo relatedTableRepo) {
        return new SimilarityService(vkReposBundle, twitterReposBundle, relatedTableRepo);
    }

    @Bean
    public VkReposBundle vkReposBundle(VKFriendsTableRepo vkFriendsTableRepo,
                                       VKPostsTableRepo vkPostsTableRepo,
                                       VKTableRepo vkTableRepo) {
        return new VkReposBundle(vkFriendsTableRepo, vkPostsTableRepo, vkTableRepo);
    }

    @Bean
    public TwitterReposBundle twitterReposBundle(TwitterFriendsTableRepo twitterFriendsTableRepo,
                    TwitterPostsTableRepo twitterPostsTableRepo,
                    TwitterTableRepo twitterTableRepo) {
        return new TwitterReposBundle(twitterFriendsTableRepo, twitterPostsTableRepo, twitterTableRepo);
    }

}
