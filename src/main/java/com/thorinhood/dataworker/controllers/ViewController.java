package com.thorinhood.dataworker.controllers;

import com.thorinhood.dataworker.repositories.related.RelatedTableRepo;
import com.thorinhood.dataworker.tables.related.RelatedTable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Controller
@RequestMapping("/api/v1")
public class ViewController {

    private final RelatedTableRepo relatedTableRepo;

    public ViewController(RelatedTableRepo relatedTableRepo) {
        this.relatedTableRepo = relatedTableRepo;
    }

    @GetMapping("/index")
    public String getIndex() {
        return "index";
    }

    @GetMapping("/assumptions")
    public String getAssumptions(@RequestParam String socialNetwork,
                                 @RequestParam String id,
                                 Map<String, Object> model) {
        if (socialNetwork.equalsIgnoreCase("vk")) {
            getAssumptions(relatedTableRepo::findByVkDomain, id, model);
        } else if (socialNetwork.equalsIgnoreCase("twitter")) {
            getAssumptions(relatedTableRepo::findByTwitter, id, model);
        }
        return "index";
    }

    private void getAssumptions(Function<String, RelatedTable> finder, String id,
                                               Map<String, Object> model) {
        RelatedTable relatedTable = finder.apply(id);
        if (relatedTable != null) {
            if (relatedTable.hasVkDomain()) {
                model.put("vk", relatedTable.getVkDomain());
            }
            if (relatedTable.hasTwittter()) {
                model.put("twitter", relatedTable.getTwitter());
            }
            if (relatedTable.hasFacebook()) {
                model.put("facebook", relatedTable.getFacebook());
            }
            if (relatedTable.hasInstagram()) {
                model.put("instagram", relatedTable.getInstagram());
            }
        }
    }

}
