package com.thorinhood.dataworker.configs;

import com.thorinhood.dataworker.cache.StringCache;
import com.thorinhood.dataworker.cache.TwitterProfilesCache;
import com.thorinhood.dataworker.db.TwitterDBService;
import com.thorinhood.dataworker.jobs.TwitterFriendsMaker;
import com.thorinhood.dataworker.repositories.posts.TwitterPostsTableRepo;
import com.thorinhood.dataworker.repositories.related.RelatedTableRepo;
import com.thorinhood.dataworker.repositories.friends.TwitterFriendsTableRepo;
import com.thorinhood.dataworker.repositories.profiles.TwitterTableRepo;
import com.thorinhood.dataworker.repositories.friends.VKFriendsTableRepo;
import com.thorinhood.dataworker.services.TwitterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

@Configuration
public class TwitterConfiguration {

    @Value("${twitter.consumer.api.key}")
    private String consumerKey;

    @Value("${twitter.consumer.api.secret}")
    private String consumerSecret;

    @Value("${twitter.access.token}")
    private String accessToken;

    @Value("${twitter.access.token.secret}")
    private String accessTokenSecret;

    @Value("${twitter.db.service.friends.threads}")
    private Integer dbServiceFriendsThreadsCount;

    @Value("${twitter.db.service.posts.threads}")
    private Integer dbServicePostsThreadsCount;

//    @Bean
//    public ConnectionFactoryLocator connectionFactoryLocator() {
//        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
//        registry.addConnectionFactory(new TwitterConnectionFactory(consumerKey, consumerSecret));
//        return registry;
//    }

    @Bean
    public TwitterTemplate twitterTemplate() {
        return new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);
    }

    @Bean
    public TwitterService twitterService(Twitter twitter) {
        return new TwitterService(twitter);
    }

    @Bean
    public TwitterProfilesCache twitterProfilesCache(TwitterDBService twitterDBService) {
        return new TwitterProfilesCache(twitterDBService);
    }

    @Bean(name = "twitterFriendsMakerCache")
    public StringCache twitterFriendsMakerCache() {
        return new StringCache("twitter friends maker cache");
    }

    @Bean
    public TwitterFriendsMaker twitterFriendsMaker(TwitterProfilesCache twitterProfilesCache,
                                                   RelatedTableRepo relatedTableRepo,
                                                   VKFriendsTableRepo vkFriendsTableRepo,
                                                   TwitterFriendsTableRepo twitterFriendsTableRepo
                                                   ) {
        return new TwitterFriendsMaker(twitterProfilesCache, relatedTableRepo, vkFriendsTableRepo,
                twitterFriendsTableRepo);
    }

    @Bean
    public TwitterDBService twitterDBService(TwitterTableRepo twitterTableRepo,
                                             TwitterFriendsTableRepo twitterFriendsTableRepo,
                                             TwitterPostsTableRepo twitterPostsTableRepo,
                                             CassandraTemplate cassandraTemplate,
                                             RelatedTableRepo relatedTableRepo,
                                             JdbcTemplate postgresJdbc) {
        return new TwitterDBService(
                twitterTableRepo,
                twitterFriendsTableRepo,
                twitterPostsTableRepo,
                cassandraTemplate,
                relatedTableRepo,
                postgresJdbc,
                dbServiceFriendsThreadsCount,
                dbServicePostsThreadsCount
        );
    }

}
