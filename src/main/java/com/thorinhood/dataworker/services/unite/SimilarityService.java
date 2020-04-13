package com.thorinhood.dataworker.services.unite;

import com.thorinhood.dataworker.repositories.TwitterReposBundle;
import com.thorinhood.dataworker.repositories.VkReposBundle;
import com.thorinhood.dataworker.repositories.friends.TwitterFriendsTableRepo;
import com.thorinhood.dataworker.repositories.friends.VKFriendsTableRepo;
import com.thorinhood.dataworker.repositories.profiles.TwitterTableRepo;
import com.thorinhood.dataworker.repositories.profiles.VKTableRepo;
import com.thorinhood.dataworker.repositories.related.RelatedTableRepo;
import com.thorinhood.dataworker.tables.friends.FriendsPair;
import com.thorinhood.dataworker.tables.posts.TwitterPostsTable;
import com.thorinhood.dataworker.tables.posts.VKPostsTable;
import com.thorinhood.dataworker.tables.profile.TwitterTable;
import com.thorinhood.dataworker.tables.profile.VKTable;
import com.thorinhood.dataworker.tables.related.RelatedTable;
import com.thorinhood.dataworker.utils.Formula;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimilarityService {

    private static final Logger logger = LoggerFactory.getLogger(SimilarityService.class);

    private RelatedTableRepo relatedTableRepo;
    private final VkReposBundle vkReposBundle;
    private final TwitterReposBundle twitterReposBundle;
    private PostsSimilarity postsSimilarity;
    private final double posts = 0.15;
    private final double location = 0.05;
    private final double friends = 0.55;
    private final double name = 0.25;
    private final double threshold = 0.3;

    public SimilarityService(VkReposBundle vkReposBundle,
                             TwitterReposBundle twitterReposBundle,
                             RelatedTableRepo relatedTableRepo) {
        this.vkReposBundle = vkReposBundle;
        this.twitterReposBundle = twitterReposBundle;
        this.relatedTableRepo = relatedTableRepo;
        postsSimilarity = new PostsSimilarity(vkReposBundle, twitterReposBundle);
    }

    public boolean match(VKTable vk) {
        if (vk == null) {
            return false;
        }
        Collection<String> pretenders = getPretenders(
            vk.vkDomain(),
            vkReposBundle.friends(),
            twitterReposBundle.friends(),
            VKFriendsTableRepo::searchByFirst,
            null,
            TwitterFriendsTableRepo::searchByFirst,
            TwitterFriendsTableRepo::searchBySecond,
            RelatedTableRepo::findByVkDomain,
            RelatedTable::getTwitter,
            twitterReposBundle.profiles(),
            TwitterTableRepo::existsByScreenName
        );
        List<TwitterTable> pretendersLoaded = pretenders.stream()
                .map(x -> twitterReposBundle.profiles().findByScreenName(x))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        TwitterTable twitter = bestSimilarity(vk, pretendersLoaded);
        if (twitter == null) {
            return false;
        }
        saveMatching(vk, twitter);
        return true;
    }

    public boolean match(TwitterTable twitter) {
        if (twitter == null) {
            return false;
        }
        Collection<String> pretenders = getPretenders(
                twitter.getScreenName(),
                twitterReposBundle.friends(),
                vkReposBundle.friends(),
                TwitterFriendsTableRepo::searchByFirst,
                TwitterFriendsTableRepo::searchBySecond,
                VKFriendsTableRepo::searchByFirst,
                null,
                RelatedTableRepo::findByTwitter,
                RelatedTable::getVkDomain,
                vkReposBundle.profiles(),
                VKTableRepo::existsByDomain
        );
        List<VKTable> pretendersLoaded = pretenders.stream()
                .map(x -> vkReposBundle.profiles().findByDomain(x))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        VKTable vk = bestSimilarity(twitter, pretendersLoaded);
        if (vk == null) {
            return false;
        }
        saveMatching(vk, twitter);
        return true;
    }

    private void saveMatching(VKTable vk, TwitterTable twitter) {
        twitter.setVk(vk.getDomain());
        vk.setTwitter(twitter.getScreenName());
        List<RelatedTable> twitterRelates = relatedTableRepo.findAllByTwitter(twitter.getScreenName())
                .stream()
                .map(table -> table.setVkDomain(vk.getDomain()))
                .collect(Collectors.toList());
        List<RelatedTable> vkRelates = relatedTableRepo.findAllByVkDomain(vk.getDomain())
                .stream()
                .map(table -> table.setTwitter(twitter.getScreenName()))
                .collect(Collectors.toList());
        vkReposBundle.profiles().save(vk);
        twitterReposBundle.profiles().save(twitter);
        relatedTableRepo.saveAll(twitterRelates);
        relatedTableRepo.saveAll(vkRelates);
    }

    private <SOURCE_FRIENDS_TABLE extends FriendsPair, TARGET_FRIENDS_TABLE extends FriendsPair,
             SOURCE_FRIENDS_TABLE_REPO, TARGET_FRIENDS_TABLE_REPO, TARGET_REPO> Collection<String> getPretenders(
                  String source,
                  SOURCE_FRIENDS_TABLE_REPO sourceFriendsTableRepo,
                  TARGET_FRIENDS_TABLE_REPO targetFriendsTableRepo,
                  BiFunction<SOURCE_FRIENDS_TABLE_REPO, String, List<SOURCE_FRIENDS_TABLE>> getSourceFriendsByFirst,
                  BiFunction<SOURCE_FRIENDS_TABLE_REPO, String, List<SOURCE_FRIENDS_TABLE>> getSourceFriendsBySecond,
                  BiFunction<TARGET_FRIENDS_TABLE_REPO, String, List<TARGET_FRIENDS_TABLE>> getTargetFriendsByFirst,
                  BiFunction<TARGET_FRIENDS_TABLE_REPO, String, List<TARGET_FRIENDS_TABLE>> getTargetFriendsBySecond,
                  BiFunction<RelatedTableRepo, String, RelatedTable> relatedTableGetter,
                  Function<RelatedTable, String> getValue,
                  TARGET_REPO targetRepo,
                  BiFunction<TARGET_REPO, String, Boolean> check) {
        Set<String> sourceFriends = getFriends(source, sourceFriendsTableRepo,
                getSourceFriendsByFirst, getSourceFriendsBySecond);
        Map<String, String> sourceToTarget = getProfilesMapping(sourceFriends, relatedTableGetter, getValue);
        Set<String> pretenders = new HashSet<>();
        sourceToTarget.values().forEach(target -> {
            pretenders.addAll(getFriends(target, targetFriendsTableRepo,
                    getTargetFriendsByFirst, getTargetFriendsBySecond));
        });
        return pretenders.stream()
            .filter(pretendent -> check.apply(targetRepo, pretendent))
            .collect(Collectors.toList());
    }

    public TwitterTable bestSimilarity(VKTable vk, Collection<TwitterTable> pretenders) {
        Set<String> vkFriends = getFriends(vk.vkDomain(), vkReposBundle.friends(),
                VKFriendsTableRepo::searchByFirst,
                null);
        Map<String, String> vkToTwitter = getProfilesMapping(vkFriends, RelatedTableRepo::findByVkDomain,
                RelatedTable::getTwitter);
        List<String> vkPosts = vkReposBundle.posts().findAllByProfileId(vk.vkDomain()).stream()
                .map(VKPostsTable::getText)
                .collect(Collectors.toList());
        TwitterTable twitter = null;
        double best = -1.d;
        int i = 0;
        for (TwitterTable pretendent : pretenders) {
            if (pretendent != null) {
                double friendsCounter = cachedVKFriendsCounted(vkToTwitter, pretendent.getScreenName());
                double locationCounter = LocationSimilarity.count(vk, pretendent);
                double postsCounter = postsSimilarity.cachedVKCount(vkPosts, pretendent.getScreenName());
                double nameCounter = NameSimilarity.count(vk, pretendent);
                double current = posts * postsCounter + location * locationCounter + friendsCounter * friends
                        + name * nameCounter;
                if (Double.compare(current, threshold) >= 0 && Double.compare(current, best) >= 0) {
                    List<RelatedTable> relatedTables = relatedTableRepo.findAllByVkDomain(pretendent.vkDomain());
                    if (relatedTables == null || relatedTables.size() == 0 || relatedTables.stream()
                            .allMatch(relatedTable -> relatedTable.getTwitter() == null ||
                                    relatedTable.getTwitter().equalsIgnoreCase("null"))) {
                        best = current;
                        twitter = pretendent;
                    }
                }
            }
        }
        return twitter;
    }

    public VKTable bestSimilarity(TwitterTable twitter, Collection<VKTable> pretenders) {
        Set<String> twitterFriends = getFriends(twitter.getScreenName(), twitterReposBundle.friends(),
                TwitterFriendsTableRepo::searchByFirst,
                TwitterFriendsTableRepo::searchBySecond);
        Map<String, String> twitterToVk = getProfilesMapping(twitterFriends, RelatedTableRepo::findByTwitter,
                RelatedTable::getVkDomain);
        List<String> twitterPosts = twitterReposBundle.posts().findAllByProfileId(twitter.getScreenName()).stream()
                .map(TwitterPostsTable::getText)
                .collect(Collectors.toList());
        VKTable vk = null;
        double best = -1.d;
        int i = 0;
        for (VKTable pretendent : pretenders) {
            if (pretendent != null) {
                double friendsCounter = cachedTwitterFriendsCounted(twitterToVk, pretendent.getDomain());
                double locationCounter = LocationSimilarity.count(pretendent, twitter);
                double postsCounter = postsSimilarity.cachedTwitterCount(twitterPosts, pretendent.getDomain());
                double nameCounter = NameSimilarity.count(pretendent, twitter);
                double current = posts * postsCounter + location * locationCounter + friendsCounter * friends
                        + name * nameCounter;
                if (Double.compare(current, threshold) >= 0 && Double.compare(current, best) >= 0) {
                    List<RelatedTable> relatedTables = relatedTableRepo.findAllByVkDomain(pretendent.vkDomain());
                    if (relatedTables == null || relatedTables.size() == 0 || relatedTables.stream()
                        .allMatch(relatedTable -> relatedTable.getTwitter() == null ||
                                  relatedTable.getTwitter().equalsIgnoreCase("null"))) {
                        best = current;
                        vk = pretendent;
                    }
                }
            }
        }
        return vk;
    }

    public Formula similarity(VKTable vkTable, TwitterTable twitterTable) {
        double friendsCounter = friendsCounted(vkTable.getDomain(), twitterTable.getId());
        double locationCounter = LocationSimilarity.count(vkTable, twitterTable);
        double postsCounter = postsSimilarity.count(vkTable.getDomain(), twitterTable.getId());
        double nameCounter = NameSimilarity.count(vkTable, twitterTable);
        return new Formula(
            Arrays.asList(posts, location, friends, name),
            Arrays.asList(postsCounter, locationCounter, friendsCounter, nameCounter)
        );
    }

    public Formula similarity(String vkDomain, String twitterId) {
        VKTable vk;
        TwitterTable twitter;
        try {
            vk = vkReposBundle.profiles().findByDomain(vkDomain);
        } catch(Exception exception) {
            return new Formula();
        }
        try {
            twitter = twitterReposBundle.profiles().findByScreenName(twitterId);
        } catch(Exception exception) {
            return new Formula();
        }
        if (vk == null) {
            return new Formula();
        } else if (twitter == null) {
            return new Formula();
        }

        return similarity(vk, twitter);
    }

    private double cachedTwitterFriendsCounted(Map<String, String> twitterToVk, String vkDomain) {
        Set<String> vkFriends = getFriends(vkDomain, vkReposBundle.friends(),
                VKFriendsTableRepo::searchByFirst,
                VKFriendsTableRepo::searchBySecond);
        Map<String, String> vkToTwitter = getProfilesMapping(vkFriends, RelatedTableRepo::findByVkDomain,
                RelatedTable::getTwitter);
        return FriendsSimilarity.count(vkToTwitter, twitterToVk);
    }

    private double cachedVKFriendsCounted(Map<String, String> vkToTwitter, String twitterId) {
        Set<String> twitterFriends = getFriends(twitterId, twitterReposBundle.friends(),
                TwitterFriendsTableRepo::searchByFirst,
                TwitterFriendsTableRepo::searchBySecond);
        Map<String, String> twitterToVk = getProfilesMapping(twitterFriends, RelatedTableRepo::findByTwitter,
                RelatedTable::getVkDomain);
        return FriendsSimilarity.count(vkToTwitter, twitterToVk);
    }


    private double friendsCounted(String vkDomain, String twitterId) {
        Set<String> vkFriends = getFriends(vkDomain, vkReposBundle.friends(),
                VKFriendsTableRepo::searchByFirst,
                VKFriendsTableRepo::searchBySecond);
        Set<String> twitterFriends = getFriends(twitterId, twitterReposBundle.friends(),
                TwitterFriendsTableRepo::searchByFirst,
                TwitterFriendsTableRepo::searchBySecond);
        Map<String, String> vkToTwitter = getProfilesMapping(vkFriends, RelatedTableRepo::findByVkDomain,
                RelatedTable::getTwitter);
        Map<String, String> twitterToVk = getProfilesMapping(twitterFriends, RelatedTableRepo::findByTwitter,
                RelatedTable::getVkDomain);
        return FriendsSimilarity.count(vkToTwitter, twitterToVk);
    }

    private Map<String, String> getProfilesMapping(Set<String> ids,
                   BiFunction<RelatedTableRepo, String, RelatedTable> relatedTableGetter,
                   Function<RelatedTable, String> getValue) {
        Map<String, String> map = new HashMap<>();
        for (String id : ids) {
            try {
                RelatedTable relatedTable = relatedTableGetter.apply(relatedTableRepo, id);
                String value = getValue.apply(relatedTable);
                if (value != null && !value.equalsIgnoreCase("null")) {
                    map.put(id, value);
                }
            } catch (Exception exception) {
            }
        }
        return map;
    }

    private <FRIENDS_TABLE_REPO, FRIENDS_TABLE extends FriendsPair> Set<String> getFriends(String id,
                                 FRIENDS_TABLE_REPO friendsTableRepo,
                                 BiFunction<FRIENDS_TABLE_REPO, String, List<FRIENDS_TABLE>> getFriendsByFirst,
                                 BiFunction<FRIENDS_TABLE_REPO, String, List<FRIENDS_TABLE>> getFriendsBySecond) {
        Set<String> first = null;
        Set<String> second = null;
        if (getFriendsByFirst != null) {
            first = getFriendsByFirst.apply(friendsTableRepo, id).stream()
                .map(FriendsPair::getSecond)
                .collect(Collectors.toSet());
        }
        if (getFriendsBySecond != null) {
            second = getFriendsBySecond.apply(friendsTableRepo, id).stream()
                .map(FriendsPair::getFirst)
                .collect(Collectors.toSet());
        }
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            first.addAll(second);
            return first;
        }
    }

}
