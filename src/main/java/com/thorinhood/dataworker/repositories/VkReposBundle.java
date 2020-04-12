package com.thorinhood.dataworker.repositories;

import com.thorinhood.dataworker.repositories.friends.VKFriendsTableRepo;
import com.thorinhood.dataworker.repositories.posts.VKPostsTableRepo;
import com.thorinhood.dataworker.repositories.profiles.VKTableRepo;

public class VkReposBundle {
    private final VKFriendsTableRepo vkFriendsTableRepo;
    private final VKPostsTableRepo vkPostsTableRepo;
    private final VKTableRepo vkTableRepo;

    public VkReposBundle(VKFriendsTableRepo vkFriendsTableRepo,
                         VKPostsTableRepo vkPostsTableRepo,
                         VKTableRepo vkTableRepo) {
        this.vkFriendsTableRepo = vkFriendsTableRepo;
        this.vkPostsTableRepo = vkPostsTableRepo;
        this.vkTableRepo = vkTableRepo;
    }

    public VKFriendsTableRepo friends() {
        return vkFriendsTableRepo;
    }

    public VKPostsTableRepo posts() {
        return vkPostsTableRepo;
    }

    public VKTableRepo profiles() {
        return vkTableRepo;
    }

}
