package com.thorinhood.dataworker.controllers;

import com.thorinhood.dataworker.repositories.related.RelatedTableRepo;
import com.thorinhood.dataworker.services.unite.SimilarityService;
import com.thorinhood.dataworker.tables.related.RelatedTable;
import com.thorinhood.dataworker.utils.common.Formula;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@RestController
@RequestMapping("/user")
public class SuggestLinksController {

    private final RelatedTableRepo relatedTableRepo;
    private final SimilarityService similarityService;

    public SuggestLinksController(RelatedTableRepo relatedTableRepo,
                                  SimilarityService similarityService) {
        this.relatedTableRepo = relatedTableRepo;
        this.similarityService = similarityService;
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

    @GetMapping("/similarity")
    public Formula getSimilarity(@RequestParam String vkDomain,
                                 @RequestParam String twitterId) {
        return similarityService.similarity(vkDomain, twitterId);
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
