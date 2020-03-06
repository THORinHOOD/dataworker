package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.tables.TwitterTable;
import com.thorinhood.dataworker.utils.common.BatchProfiles;
import com.thorinhood.dataworker.utils.common.FieldExtractor;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TwitterService implements SocialService<TwitterTable, String> {

    private Twitter twitter;

    public TwitterService(Twitter twitter) {
        this.twitter = twitter;
    }

    public Twitter getTwitter() {
        return twitter;
    }

    @Override
    public void getDefaultUsersInfo(Collection<String> userScreenNames,
                                    BlockingQueue<BatchProfiles<TwitterTable, String>> queue) {
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
            getUsersInfo(pairs, userScreenNames, 0, queue);
            queue.add(BatchProfiles.end());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getUsersInfo(Collection<FieldExtractor> pairs,
                             Collection<String> userScreenNames,
                             int depth,
                             BlockingQueue<BatchProfiles<TwitterTable, String>> queue,
                             long... userIds) throws InterruptedException {
        List<TwitterProfile> twitterProfiles = twitter.userOperations().getUsers(userScreenNames.toArray(new String[0]));
        if (userIds != null && userIds.length > 0) {
            twitterProfiles.addAll(twitter.userOperations().getUsers(userIds));
        }

        HashSet<TwitterTable> result = new HashSet<>(convert(pairs, twitterProfiles));
        queue.put(BatchProfiles.next(result));

//        while (depth > 0) {
//            getUsersInfo(
//                pairs,
//
//            );
////            for (String user : userScreenNames) {
////                try {
////                    result.addAll(convert(pairs, new ArrayList<>(twitter.friendOperations().getFriends(user))));
////                } catch (Exception exception) {
////
////                }
////            }
////            depth--;
//        }

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
