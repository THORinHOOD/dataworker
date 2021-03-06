package com.thorinhood.dataworker.loaders;

import com.google.common.collect.Lists;
import com.thorinhood.dataworker.cache.TwitterProfilesCache;
import com.thorinhood.dataworker.cache.VKProfilesCache;
import com.thorinhood.dataworker.db.TwitterDBService;
import com.thorinhood.dataworker.db.VKDBService;
import com.thorinhood.dataworker.repositories.posts.VKPostsTableRepo;
import com.thorinhood.dataworker.services.social.TwitterService;
import com.thorinhood.dataworker.services.social.VKService;
import com.thorinhood.dataworker.tables.posts.TwitterPostsTable;
import com.thorinhood.dataworker.tables.posts.VKPostsTable;
import com.thorinhood.dataworker.tables.profile.TwitterTable;
import com.thorinhood.dataworker.tables.profile.VKTable;
import com.thorinhood.dataworker.utils.MeasureTimeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Loader {

    private static final Logger logger = LoggerFactory.getLogger(Loader.class);

    private final VKService vkService;
    private final TwitterService twitterService;
    private final VKDBService vkdbService;
    private final TwitterDBService twitterDBService;
    private final VKProfilesCache vkProfilesCache;
    private final TwitterProfilesCache twitterProfilesCache;
    private final VKPostsTableRepo vkPostsTableRepo;
    private final MeasureTimeUtil measureTimeUtil;

    public Loader(VKService vkService,
                  TwitterService twitterService,
                  VKDBService vkdbService,
                  TwitterDBService twitterDBService,
                  VKProfilesCache vkProfilesCache,
                  TwitterProfilesCache twitterProfilesCache,
                  VKPostsTableRepo vkPostsTableRepo) {
        this.vkService = vkService;
        this.twitterService = twitterService;
        this.vkdbService = vkdbService;
        this.twitterDBService = twitterDBService;
        this.twitterProfilesCache = twitterProfilesCache;
        this.vkProfilesCache = vkProfilesCache;
        this.vkPostsTableRepo = vkPostsTableRepo;
        measureTimeUtil = new MeasureTimeUtil();
    }

    public void loadByTwitter(List<String> twitterIds, int depth) {
        while (depth-- > 0 && !CollectionUtils.isEmpty(twitterIds)) {
            twitterIds = measureTimeUtil.measure(this::loadByTwitterNext, twitterIds, logger,
                "loading twitter depth " + (depth + 1), twitterIds.size());
        }
    }

    public void loadByVk(List<String> vkIds, int depth) {
        while (depth-- > 0 && !CollectionUtils.isEmpty(vkIds)) {
            vkIds = measureTimeUtil.measure(this::loadByVkNext, vkIds, logger, "loading vk depth " + (depth + 1),
                    vkIds.size());
        }
    }

    public void loadVkPosts(Collection<String> vkDomain) {
        vkdbService.savePosts(vkService.getUsersPosts(vkDomain));
    }

    public void loadTwitterPosts(Collection<String> twitterIds) {
        twitterDBService.savePosts(twitterService.getUsersPosts(twitterIds));
    }

    private List<String> loadByTwitterNext(List<String> ids) {
        return twitterProfilesCache.filter(Lists.partition(ids, 25).stream())
                .flatMap(partition -> {
                    List<TwitterTable> twitterProfiles = measureTimeUtil.measure(twitterService::getUsersInfo,
                            partition, logger, "twitter profiles", partition.size());
                    twitterDBService.saveProfiles(twitterProfiles);
                    twitterDBService.savePosts(loadTweets(twitterProfiles));
                    List<String> friends = twitterProfiles.stream()
                            .flatMap(x -> {
                                if (CollectionUtils.isNotEmpty(x.getLinked())) {
                                    return x.getFriends().stream();
                                }
                                return Stream.empty();
                            })
                            .collect(Collectors.toList());
                    List<String> vkIds = twitterProfiles.stream()
                            .map(TwitterTable::vkDomain)
                            .filter(Objects::nonNull)
                            .filter(x -> !"null".equalsIgnoreCase(x))
                            .filter(x -> !vkProfilesCache.contains(x))
                            .collect(Collectors.toList());
                    List<VKTable> vkProfiles = measureTimeUtil.measure(vkService::getUsersInfo, vkIds,
                            logger, "vk profiles", vkIds.size());
                    vkdbService.saveProfiles(vkProfiles);
                    vkdbService.savePosts(loadVKPosts(vkProfiles));
                    return friends.stream();
                })
                .collect(Collectors.toList());
    }

    private List<String> loadByVkNext(List<String> ids) {
        return vkProfilesCache.filter(Lists.partition(ids, 25).stream())
            .flatMap(partition -> {
                List<VKTable> vkProfiles = measureTimeUtil.measure(vkService::getUsersInfo, partition, logger,
                        "vk profiles", partition.size());
                vkdbService.saveProfiles(vkProfiles);
                vkdbService.savePosts(loadVKPosts(vkProfiles));
                List<String> friends = vkProfiles.stream()
                        .flatMap(x -> {
                            if (CollectionUtils.isNotEmpty(x.getLinked())) {
                                return x.getFriends().stream();
                            }
                            return Stream.empty();
                        })
                        .collect(Collectors.toList());
                List<String> twitters = vkProfiles.stream()
                        .map(VKTable::getTwitter)
                        .filter(Objects::nonNull)
                        .filter(x -> !"null".equalsIgnoreCase(x))
                        .filter(x -> !twitterProfilesCache.contains(x))
                        .collect(Collectors.toList());
                List<TwitterTable> twitterProfiles = measureTimeUtil.measure(twitterService::getUsersInfo, twitters,
                        logger, "twitter profiles", twitters.size());
                twitterDBService.saveProfiles(twitterProfiles);
                twitterDBService.savePosts(loadTweets(twitterProfiles));
                return friends.stream();
            })
            .collect(Collectors.toList());
    }

    private List<TwitterPostsTable> loadTweets(List<TwitterTable> twitterProfiles) {
        return twitterService.getUsersPosts(twitterProfiles.stream()
            .map(TwitterTable::getScreenName)
            .collect(Collectors.toList()));
    }

    private List<VKPostsTable> loadVKPosts(List<VKTable> vkProfiles) {
        return vkService.getUsersPosts(vkProfiles.stream()
            .map(VKTable::getDomain)
            .collect(Collectors.toList()));
    }

}
