package com.thorinhood.dataworker.controllers;

import com.thorinhood.dataworker.db.TwitterDBService;
import com.thorinhood.dataworker.db.VKDBService;
import com.thorinhood.dataworker.loaders.Loader;
import com.thorinhood.dataworker.repositories.TwitterReposBundle;
import com.thorinhood.dataworker.repositories.VkReposBundle;
import com.thorinhood.dataworker.repositories.related.RelatedTableRepo;
import com.thorinhood.dataworker.services.social.TwitterService;
import com.thorinhood.dataworker.services.social.VKService;
import com.thorinhood.dataworker.tables.profile.TwitterTable;
import com.thorinhood.dataworker.tables.related.RelatedTable;
import com.thorinhood.dataworker.utils.common.CallbackExecutor;
import com.thorinhood.dataworker.utils.common.CallbackRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private final TwitterReposBundle twitterReposBundle;
    private final VkReposBundle vkReposBundle;
    private final RelatedTableRepo relatedTableRepo;
    private CallbackExecutor callbackExecutor;

    public LoaderController(Loader loader,
                            VKService vkService,
                            TwitterService twitterService,
                            VKDBService vkdbService,
                            TwitterDBService twitterDBService,
                            TwitterReposBundle twitterReposBundle,
                            VkReposBundle vkReposBundle,
                            RelatedTableRepo relatedTableRepo) {
        this.loader = loader;
        this.vkService = vkService;
        this.twitterService = twitterService;
        this.vkdbService = vkdbService;
        this.twitterDBService = twitterDBService;
        this.twitterReposBundle = twitterReposBundle;
        this.vkReposBundle = vkReposBundle;
        this.relatedTableRepo = relatedTableRepo;
        callbackExecutor = new CallbackExecutor();
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

    @GetMapping("/vk/load/posts")
    public void loadVkPosts(@RequestParam List<String> ids) {
        if (!executing) {
            executing = true;
            loader.loadVkPosts(ids);
            executing = false;
        }
    }

    @GetMapping("/twitter/load/posts")
    public void loadTwitterPosts(@RequestParam List<String> ids) {
        if (!executing) {
            executing = true;
            loader.loadTwitterPosts(ids);
            executing = false;
        }
    }

    @GetMapping("/stop")
    public void stop() {
        if (executing) {
            callbackExecutor.stop();
            executing = false;
        }
    }

    @GetMapping("/twitter/start")
    public void startTwitter(@RequestParam List<String> ids,
                             @RequestParam int depth) {
        if (!executing) {
            executing = true;
            callbackExecutor.execute(new CallbackRunnable() {
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
                    loader.loadByTwitter(ids, depth);
                }
            });
        }
    }

    @GetMapping("/vk/start")
    public void startVK(@RequestParam List<String> ids,
                        @RequestParam int depth) {
        if (!executing) {
            executing = true;
            callbackExecutor.execute(new CallbackRunnable() {
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
                    loader.loadByVk(ids, depth);
                }
            });
        }
    }

}
