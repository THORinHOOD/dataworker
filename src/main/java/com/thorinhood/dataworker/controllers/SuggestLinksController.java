package com.thorinhood.dataworker.controllers;

import com.thorinhood.dataworker.repositories.TwitterReposBundle;
import com.thorinhood.dataworker.repositories.VkReposBundle;
import com.thorinhood.dataworker.repositories.related.RelatedTableRepo;
import com.thorinhood.dataworker.db.VKDBService;
import com.thorinhood.dataworker.tables.profile.TwitterTable;
import com.thorinhood.dataworker.utils.common.Formula;
import com.thorinhood.dataworker.services.unite.SimilarityService;
import com.thorinhood.dataworker.tables.related.RelatedTable;
import com.thorinhood.dataworker.tables.profile.VKTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class SuggestLinksController {

    private final static Logger logger = LoggerFactory.getLogger(SuggestLinksController.class);

    private final VKDBService dbService;
    private final RelatedTableRepo relatedTableRepo;
    private final SimilarityService similarityService;
    private final TwitterReposBundle twitterReposBundle;
    private final VkReposBundle vkReposBundle;

    public SuggestLinksController(VKDBService dbService,
                                  RelatedTableRepo relatedTableRepo,
                                  SimilarityService similarityService,
                                  TwitterReposBundle twitterReposBundle,
                                  VkReposBundle vkReposBundle) {
        this.dbService = dbService;
        this.relatedTableRepo = relatedTableRepo;
        this.similarityService = similarityService;
        this.twitterReposBundle = twitterReposBundle;
        this.vkReposBundle = vkReposBundle;
    }

    @GetMapping("/vk")
    public VKTable getVKUser(@RequestParam String id) {
        return dbService.getPageById(id).orElse(null);
    }

    @GetMapping("/assumptions")
    public Map<String, String> getAssumptions(@RequestParam String socialNetwork,
                                              @RequestParam String id) {
        if (socialNetwork.equalsIgnoreCase("vk")) {
            return getAssumptions(relatedTableRepo::findByVkDomain, id);
        } else if (socialNetwork.equalsIgnoreCase("twitter")) {
            return getAssumptions(relatedTableRepo::findByTwitter, id);
        } else {
            return Collections.emptyMap();
        }
    }

    @GetMapping("/update")
    public void update() {
        logger.info("Start updating twitter...");
        twitterReposBundle.profiles().findAll().forEach(table -> {
            RelatedTable relatedTable = relatedTableRepo.findByTwitter(table.getId());
            if (relatedTable != null) {
                table.setVk(relatedTable.getVkDomain());
                twitterReposBundle.profiles().save(table);
                VKTable vkTable = vkReposBundle.profiles().findByDomain(relatedTable.getVkDomain());
                if (vkTable != null) {
                    vkTable.setTwitter(table.getId());
                    vkReposBundle.profiles().save(vkTable);
                }
            }
//            table.setVk("null");
//            twitterReposBundle.profiles().save(table);
        });
        logger.info("End updating twitter...");
    }

    @GetMapping("/match")
    public boolean tryMatch(@RequestParam String vkDomain) {
        return similarityService.match(vkDomain);
    }

    @GetMapping("/similarity")
    public Formula getSimilarity(@RequestParam String vkDomain,
                                 @RequestParam String twitterId) {
        return similarityService.similarity(vkDomain, twitterId);
    }

    @GetMapping("/test")
    public List<Formula> check() {
        List<Formula> sims = new ArrayList<>();
        List<String> twitters = twitterReposBundle.profiles().findAll().stream()
            .map(TwitterTable::getScreenName)
            .collect(Collectors.toList());
        int count = 0;
        for (String twitter : twitters) {
            try {
                RelatedTable relatedTable = relatedTableRepo.findByTwitter(twitter);
                if (relatedTable != null && relatedTable.getVkDomain() != null
                        && !relatedTable.getVkDomain().equalsIgnoreCase("null")) {
                    sims.add(similarityService.similarity(relatedTable.getVkDomain(), twitter));
                }
            } catch(Exception exception) {

            }
            logger.info("PROCESSED " + ++count + "/" + twitters.size());
        }
        return sims;
    }


    private Map<String, String> getAssumptions(Function<String, RelatedTable> finder, String id) {
        RelatedTable relatedTable = finder.apply(id);
        if (relatedTable == null) {
            return Collections.emptyMap();
        } else {
            Map<String, String> assumptions = new HashMap<>();
            assumptions.put("vk_id", String.valueOf(relatedTable.getVkId()));
            assumptions.put("vk_domain", relatedTable.getVkDomain());
            assumptions.put("twitter", relatedTable.getTwitter());
            assumptions.put("facebook", relatedTable.getFacebook());
            assumptions.put("instagram", relatedTable.getInstagram());
            return assumptions;
        }
    }

}
