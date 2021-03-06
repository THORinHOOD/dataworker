package com.thorinhood.dataworker.controllers;

import com.thorinhood.dataworker.db.TwitterDBService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/twitter")
public class TwitterController {

    private final TwitterDBService twitterDBService;

    public TwitterController(TwitterDBService twitterDBService) {
        this.twitterDBService = twitterDBService;
    }

    @GetMapping("/allProfiles/count")
    public long countTwitter() {
        return twitterDBService.countAllProfiles();
    }

    @GetMapping("/allProfiles/friends/count")
    public long countFriendsVk() {
        return twitterDBService.countFriends();
    }

    @GetMapping("/allProfiles/posts/count")
    public long countPosts() {
        return twitterDBService.countPosts();
    }

}
