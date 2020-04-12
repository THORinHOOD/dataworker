package com.thorinhood.dataworker.jobs;

import com.thorinhood.dataworker.repositories.TwitterReposBundle;
import com.thorinhood.dataworker.repositories.VkReposBundle;
import com.thorinhood.dataworker.repositories.related.RelatedTableRepo;
import com.thorinhood.dataworker.services.unite.SimilarityService;
import com.thorinhood.dataworker.tables.profile.TwitterTable;
import com.thorinhood.dataworker.tables.profile.VKTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VkUpdater {

    private static final Logger logger = LoggerFactory.getLogger(VkUpdater.class);

    private final SimilarityService similarityService;
    private final RelatedTableRepo relatedTableRepo;
    private final ExecutorService executorService;
    private final VkReposBundle vkReposBundle;
    private final TwitterReposBundle twitterReposBundle;

    public VkUpdater(SimilarityService similarityService, RelatedTableRepo relatedTableRepo,
                     VkReposBundle vkReposBundle, TwitterReposBundle twitterReposBundle) {
        this.similarityService = similarityService;
        this.relatedTableRepo = relatedTableRepo;
        executorService = Executors.newFixedThreadPool(10);
        this.vkReposBundle = vkReposBundle;
        this.twitterReposBundle = twitterReposBundle;
    }

    @Scheduled(fixedRate = 3600 * 1000L)
    public void update() {
        logger.info("Start updating vk to twitter relations...");
        List<VKTable> vkTables = vkReposBundle.profiles().findAllByTwitter("null");
        for (int i = 0; i < vkTables.size(); i++) {
            boolean success = similarityService.match(vkTables.get(i));
            logger.info("SUCCESSFULL match [" + success + "] for " + vkTables.get(i).getDomain() + " "
                    + (i + 1) + "/" + vkTables.size());
        }
        logger.info("Stop updating vk to twitter relations...");
    }

}
