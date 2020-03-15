package com.thorinhood.dataworker.configs;

import com.thorinhood.dataworker.repositories.RelatedTableRepo;
import com.thorinhood.dataworker.repositories.TwitterFriendsTableRepo;
import com.thorinhood.dataworker.repositories.TwitterTableRepo;
import com.thorinhood.dataworker.services.TwitterService;
import com.thorinhood.dataworker.services.db.TwitterDBService;
import com.thorinhood.dataworker.services.cache.TwitterProfilesCache;
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

    @Value("twitter.db.service.friends.threads")
    private Integer dbServiceFriendsThreadsCount;

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

    @Bean
    public TwitterDBService twitterDBService(TwitterTableRepo twitterTableRepo,
                                             TwitterFriendsTableRepo twitterFriendsTableRepo,
                                             CassandraTemplate cassandraTemplate,
                                             RelatedTableRepo relatedTableRepo,
                                             JdbcTemplate postgresJdbc) {
        return new TwitterDBService(
                twitterTableRepo,
                twitterFriendsTableRepo,
                cassandraTemplate,
                relatedTableRepo,
                postgresJdbc,
                dbServiceFriendsThreadsCount
        );
    }

}
