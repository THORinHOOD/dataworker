package com.thorinhood.dataworker.controllers;

import com.thorinhood.dataworker.db.VKDBService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vk")
public class VkController {

    private final VKDBService vkdbService;

    public VkController(VKDBService vkdbService) {
        this.vkdbService = vkdbService;
    }

    @GetMapping("/vk/allProfiles/count")
    public long countVk() {
        return vkdbService.countAllProfiles();
    }

    @GetMapping("/vk/allProfiles/friends/count")
    public long countFriendsVk() {
        return vkdbService.countFriends();
    }


}
