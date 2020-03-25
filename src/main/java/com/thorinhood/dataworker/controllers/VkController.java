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

    @GetMapping("/allProfiles/count")
    public long countVk() {
        return vkdbService.countAllProfiles();
    }

    @GetMapping("/allProfiles/friends/count")
    public long countFriendsVk() {
        return vkdbService.countFriends();
    }

    @GetMapping("/allProfiles/posts/count")
    public long countPosts() {
        return vkdbService.countPosts();
    }

}
