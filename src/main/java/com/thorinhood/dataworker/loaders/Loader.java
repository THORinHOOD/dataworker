package com.thorinhood.dataworker.loaders;

import com.google.common.collect.Lists;
import com.thorinhood.dataworker.services.TwitterService;
import com.thorinhood.dataworker.services.VKService;
import com.thorinhood.dataworker.services.db.TwitterDBService;
import com.thorinhood.dataworker.services.cache.TwitterProfilesCache;
import com.thorinhood.dataworker.services.db.VKDBService;
import com.thorinhood.dataworker.services.cache.VKProfilesCache;
import com.thorinhood.dataworker.tables.TwitterTable;
import com.thorinhood.dataworker.tables.VKTable;
import com.thorinhood.dataworker.utils.common.MeasureTimeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public Loader(VKService vkService,
                  TwitterService twitterService,
                  VKDBService vkdbService,
                  TwitterDBService twitterDBService,
                  VKProfilesCache vkProfilesCache,
                  TwitterProfilesCache twitterProfilesCache) {
        this.vkService = vkService;
        this.twitterService = twitterService;
        this.vkdbService = vkdbService;
        this.twitterDBService = twitterDBService;
        this.twitterProfilesCache = twitterProfilesCache;
        this.vkProfilesCache = vkProfilesCache;
    }

    public void load(List<String> vkIds, int depth) {
        MeasureTimeUtil measureTimeUtil = new MeasureTimeUtil();
        vkIds = vkIds.stream()
            .filter(x -> !vkProfilesCache.contains(x))
            .collect(Collectors.toList());
        while (depth-- > 0) {
            vkIds = measureTimeUtil.measure(this::loadNext, vkIds, logger, "loading vk depth " + (depth + 1));
        }
    }

    private List<String> loadNext(List<String> ids) {
        return vkProfilesCache.filter(Lists.partition(ids, 25).stream())
            .flatMap(partition -> {
                List<VKTable> vkProfiles = vkService.getUsersInfo(partition);
                vkdbService.saveProfiles(vkProfiles);
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
                List<TwitterTable> twitterProfiles = twitterService.getUsersInfo(twitters);
                twitterDBService.saveProfiles(twitterProfiles);
                return friends.stream();
            })
            .collect(Collectors.toList());
    }

}
