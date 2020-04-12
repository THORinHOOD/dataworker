package com.thorinhood.dataworker.jobs;

import com.thorinhood.dataworker.repositories.TwitterReposBundle;
import com.thorinhood.dataworker.repositories.VkReposBundle;
import com.thorinhood.dataworker.repositories.related.RelatedTableRepo;
import com.thorinhood.dataworker.services.unite.SimilarityService;
import com.thorinhood.dataworker.tables.profile.TwitterTable;
import com.thorinhood.dataworker.tables.profile.VKTable;
import com.thorinhood.dataworker.tables.related.RelatedTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TwitterUpdater {
    private static final Logger logger = LoggerFactory.getLogger(TwitterUpdater.class);

    private final SimilarityService similarityService;
    private final RelatedTableRepo relatedTableRepo;
    private final TwitterReposBundle twitterReposBundle;
    private final ExecutorService executorService;

    public TwitterUpdater(SimilarityService similarityService,
                          RelatedTableRepo relatedTableRepo,
                          TwitterReposBundle twitterReposBundle) {
        this.similarityService = similarityService;
        this.relatedTableRepo = relatedTableRepo;
        this.twitterReposBundle = twitterReposBundle;
        this.executorService = Executors.newFixedThreadPool(10);
    }

    @Scheduled(fixedRate = 3600 * 1000L)
    public void update() {
        logger.info("Start updating twitter to vk relations...");
        List<TwitterTable> twitterTables = twitterReposBundle.profiles().findAllByVk("null");
        for (int i = 0; i < twitterTables.size(); i++) {
            boolean success = similarityService.match(twitterTables.get(i));
            logger.info("SUCCESSFULL match [" + success + "] for " + twitterTables.get(i).getScreenName() + " "
                    + (i + 1) + "/" + twitterTables.size());
        }
        logger.info("Stop updating twitter to vk relations...");
    }


}
