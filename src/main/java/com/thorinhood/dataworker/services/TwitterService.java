package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.tables.TwitterTable;
import com.thorinhood.dataworker.utils.common.Extractor;
import com.thorinhood.dataworker.utils.common.PersonInfo;
import com.thorinhood.dataworker.utils.common.SocialService;
import com.thorinhood.dataworker.utils.twitter.TwitterUserPair;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;

import java.util.*;
import java.util.stream.Collectors;

public class TwitterService implements SocialService<TwitterTable> {

    private Twitter twitter;

    public TwitterService(Twitter twitter) {
        this.twitter = twitter;
    }

    public Collection<TwitterTable> getDefaultUsersInfo(Collection<String> userScreenNames) {
        List<TwitterUserPair> pairs = List.of(
            pair("screenName", TwitterProfile::getScreenName),
            pair("name", TwitterProfile::getName),
            pair("profileImageUrl", TwitterProfile::getProfileImageUrl),
            pair("description", TwitterProfile::getDescription),
            pair("location", TwitterProfile::getLocation),
            pair("createdDate", TwitterProfile::getCreatedDate),
            pair("language", TwitterProfile::getLanguage),
            pair("friendsCount", TwitterProfile::getFriendsCount),
            pair("followersCount", TwitterProfile::getFollowersCount)
        );

        return getUsersInfo(pairs, userScreenNames);
    }

    public List<TwitterTable> getUsersInfo(Collection<TwitterUserPair> pairs,
                                         Collection<String> userScreenNames,
                                         long... userIds) {
        List<TwitterProfile> twitterProfiles = twitter.userOperations().getUsers(userScreenNames.toArray(new String[0]));
        if (userIds != null && userIds.length > 0) {
            twitterProfiles.addAll(twitter.userOperations().getUsers(userIds));
        }
        return twitterProfiles.stream()
                .map(profile -> pairs.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(TwitterUserPair::getKey, pair -> pair.extract(profile))))
                .map(PersonInfo::new)
                .collect(Collectors.toList());
    }

    private TwitterUserPair pair(String key, Extractor<TwitterProfile> extractor) {
        return TwitterUserPair.newBuilder()
                .key(key)
                .extractor(extractor)
                .build();
    }

}
