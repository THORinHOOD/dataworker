package com.thorinhood.dataworker.controllers;

import com.thorinhood.dataworker.loaders.Loader;
import com.thorinhood.dataworker.services.TwitterService;
import com.thorinhood.dataworker.services.VKService;
import com.thorinhood.dataworker.db.TwitterDBService;
import com.thorinhood.dataworker.db.VKDBService;
import com.thorinhood.dataworker.utils.common.CallbackExecutor;
import com.thorinhood.dataworker.utils.common.CallbackRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/loader")
public class LoaderController {

    private static final Logger logger = LoggerFactory.getLogger(Loader.class);

    private final Loader loader;
    private final VKService vkService;
    private final TwitterService twitterService;
    private final VKDBService vkdbService;
    private final TwitterDBService twitterDBService;
    private boolean executing;

    public LoaderController(Loader loader,
                            VKService vkService,
                            TwitterService twitterService,
                            VKDBService vkdbService,
                            TwitterDBService twitterDBService) {
        this.loader = loader;
        this.vkService = vkService;
        this.twitterService = twitterService;
        this.vkdbService = vkdbService;
        this.twitterDBService = twitterDBService;
    }

    @GetMapping("/vk/allProfiles/count")
    public long countVk() {
        return vkdbService.countAllProfiles();
    }

    @GetMapping("/twitter/allProfiles/count")
    public long countTwitter() {
        return twitterDBService.countAllProfiles();
    }

    @GetMapping("/vk/allProfiles/friends/count")
    public long countFriendsVk() {
        return vkdbService.countFriends();
    }

    @GetMapping("/truncate")
    public void truncateAll() {
        vkdbService.truncateAll();
        twitterDBService.truncateAll();
    }

    @GetMapping("/canstart")
    public boolean canStart() {
        return !executing;
    }

    @GetMapping("/vk/start")
    public void start(@RequestParam List<String> ids,
                      @RequestParam int depth) {
        if (!executing) {
            executing = true;
            new CallbackExecutor().execute(new CallbackRunnable() {
                @Override
                public void callback() {
                    logger.info("Loader ended");
                    executing = false;
                }

                @Override
                public void error(Exception e) {
                    logger.error("Loader failed with exception", e);
                    executing = false;
                }

                @Override
                public void run() {
                    loader.load(ids, depth);
                }
            });
        }
    }



}
