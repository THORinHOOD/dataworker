package com.thorinhood.dataworker.controllers;

import com.thorinhood.dataworker.loaders.Loader;
import com.thorinhood.dataworker.repositories.RelatedTableRepo;
import com.thorinhood.dataworker.services.db.VKDBService;
import com.thorinhood.dataworker.tables.RelatedTable;
import com.thorinhood.dataworker.tables.VKTable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class SuggestLinksController {

    private final VKDBService dbService;
    private final RelatedTableRepo relatedTableRepo;
    private final Loader loader;

    public SuggestLinksController(VKDBService dbService,
                                  RelatedTableRepo relatedTableRepo,
                                  Loader loader) {
        this.dbService = dbService;
        this.relatedTableRepo = relatedTableRepo;
        this.loader = loader;
    }

    @GetMapping("/vk")
    public VKTable getVKUser(@RequestParam String id) {
        return dbService.getPageById(id).orElse(null);
    }

    @GetMapping("/vk/start")
    public void start(@RequestParam List<String> ids,
                      @RequestParam int depth) {
        new Thread(() -> loader.load(ids, depth)).start();
    }

    @GetMapping("/assumptions")
    public Map<String, String> getAssumptions(@RequestParam String socialNetwork,
                                              @RequestParam String id) {
        if (socialNetwork.equalsIgnoreCase("vk")) {
            RelatedTable relatedTable = relatedTableRepo.findByVkDomain(id);
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

        } else {
            return Collections.emptyMap();
        }
    }

}
