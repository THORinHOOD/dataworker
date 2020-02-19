package com.thorinhood.dataworker.controllers;

import com.thorinhood.dataworker.services.VKDBService;
import com.thorinhood.dataworker.tables.VKTable;
import com.thorinhood.dataworker.utils.vk.VKDataUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class SuggestLinksController {

    private final VKDBService dbService;

    public SuggestLinksController(VKDBService dbService) {
        this.dbService = dbService;
    }

    @GetMapping("/vk")
    public VKTable getVKUser(@RequestParam String id) {
        return dbService.getPageById(id).orElse(null);
    }

    @GetMapping("/assumptions")
    public Map<String, String> getAssumptions(@RequestParam String socialNetwork,
                                              @RequestParam String id) {
        if (socialNetwork.equalsIgnoreCase("vk")) {
            Optional<VKTable> vkTableOptional = dbService.getPageById(id);
            if (vkTableOptional.isEmpty()) {
                return Collections.emptyMap();
            } else {
                Map<String, String> assumptions = new HashMap<>();
                VKDataUtil.getAllAssumptions(assumptions, vkTableOptional.get());
                return assumptions;
            }
        } else {
            return Collections.emptyMap();
        }
    }

}
