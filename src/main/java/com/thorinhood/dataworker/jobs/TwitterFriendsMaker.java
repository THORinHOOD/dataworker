package com.thorinhood.dataworker.jobs;

import com.thorinhood.dataworker.cache.TwitterProfilesCache;
import com.thorinhood.dataworker.repositories.related.RelatedTableRepo;
import com.thorinhood.dataworker.repositories.friends.TwitterFriendsTableRepo;
import com.thorinhood.dataworker.repositories.friends.VKFriendsTableRepo;
import com.thorinhood.dataworker.tables.related.RelatedTable;
import com.thorinhood.dataworker.tables.friends.TwitterFriendsTable;
import com.thorinhood.dataworker.tables.friends.VKFriendsTable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TwitterFriendsMaker {

    private final RelatedTableRepo relatedTableRepo;
    private final VKFriendsTableRepo vkFriendsTableRepo;
    private final TwitterFriendsTableRepo twitterFriendsTableRepo;
    private final ExecutorService executorService;

    public TwitterFriendsMaker(TwitterProfilesCache twitterProfilesCache,
                               RelatedTableRepo relatedTableRepo,
                               VKFriendsTableRepo vkFriendsTableRepo,
                               TwitterFriendsTableRepo twitterFriendsTableRepo) {
        this.relatedTableRepo = relatedTableRepo;
        this.vkFriendsTableRepo = vkFriendsTableRepo;
        this.twitterFriendsTableRepo = twitterFriendsTableRepo;
        executorService = Executors.newFixedThreadPool(10);
        twitterProfilesCache.subscribeOnSave(this::linker);
    }

    public void linker(Collection<String> ids) {
        executorService.submit(() -> {
            Stream<RelatedTable> relatedTableList = convertToRelated(ids);
            Map<RelatedTable, List<String>> vkFriends = getVkFriends(relatedTableList);
            vkFriends.forEach((relatedTable, vkFriendsDomains) -> {
                List<String> twitters = getVkToTwitter(vkFriendsDomains);
                twitters.forEach(twitter -> twitterFriendsTableRepo.save(new TwitterFriendsTable()
                        .setKey(relatedTable.getTwitter(), twitter)));
            });
        });
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
                relatedTable -> vkFriendsTableRepo.searchByFirst(relatedTable.getVkDomain())
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
