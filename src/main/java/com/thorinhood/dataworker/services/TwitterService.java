package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.tables.friends.TwitterFriendsTable;
import com.thorinhood.dataworker.tables.posts.TwitterPostsTable;
import com.thorinhood.dataworker.tables.profile.TwitterTable;
import com.thorinhood.dataworker.utils.common.FieldExtractor;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TwitterService extends SocialService<TwitterTable, TwitterPostsTable, String, TwitterFriendsTable> {

    private Twitter twitter;
    private final List<FieldExtractor> pairs = List.of(
            pair("screenName", TwitterProfile::getScreenName, TwitterTable::setScreenName),
            pair("name", TwitterProfile::getName, TwitterTable::setName),
            pair("profileImageUrl", TwitterProfile::getProfileImageUrl, TwitterTable::setProfileImageUrl),
            pair("description", TwitterProfile::getDescription, TwitterTable::setDescription),
            pair("location", TwitterProfile::getLocation, TwitterTable::setLocation),
            pair("createdDate", x -> x.getCreatedDate().toString(), TwitterTable::setCreatedDate),
            pair("language", TwitterProfile::getLanguage, TwitterTable::setLanguage),
            pair("friendsCount", TwitterProfile::getFriendsCount, TwitterTable::setFriendsCount),
            pair("followersCount", TwitterProfile::getFollowersCount, TwitterTable::setFollowersCount)
    );

    public TwitterService(Twitter twitter) {
        super(TwitterService.class);
        this.twitter = twitter;
    }

    @Override
    public List<TwitterTable> getUsersInfo(List<String> users) {
        if (CollectionUtils.isEmpty(users)) {
            return Collections.emptyList();
        }

        List<TwitterTable> result = Collections.emptyList();
        try {
            result = getUsersInfo(pairs, users);
        } catch(Exception e) {
            logger.error("Error [twitter profiles loading] : " + users.toString(), e);
        }
        return result;
    }

    @Override
    public List<TwitterPostsTable> getUsersPosts(Collection<String> strings) {
        return null;
    }

    public Twitter getTwitter() {
        return twitter;
    }

    private List<TwitterTable> getUsersInfo(Collection<FieldExtractor> pairs,
                             Collection<String> userScreenNames) throws InterruptedException {
        List<TwitterProfile> twitterProfiles = twitter.userOperations()
                .getUsers(userScreenNames.toArray(String[]::new));
        Map<String, TwitterTable> profiles = convert(pairs, twitterProfiles)
                .stream()
                .collect(Collectors.toMap(TwitterTable::getScreenName, Function.identity()));
        return new ArrayList<>(profiles.values());
    }

    private Collection<TwitterTable> convert(Collection<FieldExtractor> pairs, Collection<TwitterProfile> profiles) {
        return profiles.stream()
                .map(profile -> {
                    TwitterTable twitterTable = new TwitterTable();
                    pairs.stream()
                            .filter(Objects::nonNull)
                            .forEach(pair -> pair.process(profile, twitterTable));
                    return twitterTable;
                })
                .collect(Collectors.toList());
    }

    private <TYPE> FieldExtractor<TwitterProfile, TwitterTable, TYPE> pair(String key,
                                                                           Function<TwitterProfile, TYPE> extractor,
                                                                           BiConsumer<TwitterTable, TYPE> setter) {
        return FieldExtractor.<TwitterProfile, TwitterTable, TYPE>newBuilder()
                .setSetter(setter)
                .setExtractor(extractor)
                .setKey(key)
                .build();
    }

}
