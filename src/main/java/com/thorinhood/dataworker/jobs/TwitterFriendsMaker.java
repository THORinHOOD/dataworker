package com.thorinhood.dataworker.jobs;

import com.thorinhood.dataworker.cache.StringCache;
import com.thorinhood.dataworker.cache.TwitterProfilesCache;
import com.thorinhood.dataworker.db.TwitterDBService;
import com.thorinhood.dataworker.repositories.RelatedTableRepo;
import com.thorinhood.dataworker.repositories.TwitterFriendsTableRepo;
import com.thorinhood.dataworker.repositories.VKFriendsTableRepo;
import com.thorinhood.dataworker.tables.RelatedTable;
import com.thorinhood.dataworker.tables.TwitterFriendsTable;
import com.thorinhood.dataworker.tables.VKFriendsTable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TwitterFriendsMaker {

    private final RelatedTableRepo relatedTableRepo;
    private final VKFriendsTableRepo vkFriendsTableRepo;
    private final TwitterFriendsTableRepo twitterFriendsTableRepo;

    public TwitterFriendsMaker(TwitterProfilesCache twitterProfilesCache,
                               RelatedTableRepo relatedTableRepo,
                               VKFriendsTableRepo vkFriendsTableRepo,
                               TwitterFriendsTableRepo twitterFriendsTableRepo) {
        this.relatedTableRepo = relatedTableRepo;
        this.vkFriendsTableRepo = vkFriendsTableRepo;
        this.twitterFriendsTableRepo = twitterFriendsTableRepo;
        twitterProfilesCache.subscribeOnSave(this::linker);
    }

    public void linker(Collection<String> ids) {
        new Thread(() -> {
            Stream<RelatedTable> relatedTableList = convertToRelated(ids);
            Map<RelatedTable, List<String>> vkFriends = getVkFriends(relatedTableList);
            vkFriends.forEach((relatedTable, vkFriendsDomains) -> {
                List<String> twitters = getVkToTwitter(vkFriendsDomains);
                twitters.forEach(twitter -> twitterFriendsTableRepo.save(new TwitterFriendsTable()
                        .setKey(relatedTable.getTwitter(), twitter)));
            });
        }).start();
    }

    private List<String> getVkToTwitter(List<String> vkDomains) {
        return vkDomains.stream()
            .map(vkDomain -> {
                RelatedTable relatedTable = relatedTableRepo.findByVkDomain(vkDomain);
                return relatedTable == null ||
                       relatedTable.getTwitter() == null ||
                       relatedTable.getTwitter().equalsIgnoreCase("null") ?
                       null : relatedTable.getTwitter();
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private Map<RelatedTable, List<String>> getVkFriends(Stream<RelatedTable> relatedTables) {
        return relatedTables.collect(Collectors.toMap(
                Function.identity(),
                relatedTable -> vkFriendsTableRepo.findAllByFirst(relatedTable.getVkDomain())
                    .stream()
                    .map(VKFriendsTable::getSecond)
                    .collect(Collectors.toList())
            ));
    }

    private Stream<RelatedTable> convertToRelated(Collection<String> ids) {
        return ids.stream()
            .map(relatedTableRepo::findByTwitter)
            .filter(Objects::nonNull)
            .filter(related -> related.getVkDomain() != null)
            .filter(related -> !related.getVkDomain().equalsIgnoreCase("null"));
    }

}
