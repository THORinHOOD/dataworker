package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.tables.TwitterFriendsTable;
import com.thorinhood.dataworker.tables.TwitterTable;
import com.thorinhood.dataworker.utils.common.BatchProfiles;
import com.thorinhood.dataworker.utils.common.FieldExtractor;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TwitterService extends SocialService<TwitterTable, String, TwitterFriendsTable> {

    private Twitter twitter;

    public TwitterService(Twitter twitter) {
        super(TwitterService.class);
        this.twitter = twitter;
    }

    @Override
    public List<TwitterTable> getUsersInfo(List<String> users) {
        if (CollectionUtils.isEmpty(users)) {
            return Collections.emptyList();
        }
        List<FieldExtractor> pairs = List.of(
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

        try {
            return getUsersInfo(pairs, users);
        } catch (Exception e) {
            logger.error("While loading twitter profiles : " + users.toString(), e);
            return Collections.emptyList();
        }
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public List<TwitterTable> getUsersInfo(Collection<FieldExtractor> pairs,
                             Collection<String> userScreenNames) throws InterruptedException {
        logger.info("Start loading twitter profiles : " + userScreenNames.size());
        List<TwitterProfile> twitterProfiles = twitter.userOperations().getUsers(userScreenNames.toArray(String[]::new));

        Map<String, TwitterTable> profiles = convert(pairs, twitterProfiles)
                .stream()
                .collect(Collectors.toMap(TwitterTable::getScreenName, Function.identity()));
        ArrayList<TwitterTable> result = new ArrayList<>();
//        profiles.keySet().forEach(id -> {
//            try {
//                result.addAll(convert(pairs, twitter.userOperations().getUsers(
//                    twitter.friendOperations().getFriendIds(id).stream().mapToLong(Long::longValue).toArray()
//                )));
//                profiles.get(id).setFriends(new ArrayList<>());
//            } catch(Exception exception) {
//                logger.error("Can't get twitter friends : " + id);
//            }
//        });
        result.addAll(profiles.values());
        logger.info("Ended loading twitter profiles : " + userScreenNames.size());
        return result;
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
