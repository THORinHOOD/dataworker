package com.thorinhood.dataworker.repositories;

import com.thorinhood.dataworker.repositories.friends.TwitterFriendsTableRepo;
import com.thorinhood.dataworker.repositories.friends.VKFriendsTableRepo;
import com.thorinhood.dataworker.repositories.posts.TwitterPostsTableRepo;
import com.thorinhood.dataworker.repositories.posts.VKPostsTableRepo;
import com.thorinhood.dataworker.repositories.profiles.TwitterTableRepo;
import com.thorinhood.dataworker.repositories.profiles.VKTableRepo;
import com.thorinhood.dataworker.repositories.related.RelatedTableRepo;

public class TwitterReposBundle {

    private final TwitterFriendsTableRepo twitterFriendsTableRepo;
    private final TwitterPostsTableRepo twitterPostsTableRepo;
    private final TwitterTableRepo twitterTableRepo;

    public TwitterReposBundle(TwitterFriendsTableRepo twitterFriendsTableRepo,
                              TwitterPostsTableRepo twitterPostsTableRepo,
                              TwitterTableRepo twitterTableRepo) {
        this.twitterFriendsTableRepo = twitterFriendsTableRepo;
        this.twitterPostsTableRepo = twitterPostsTableRepo;
        this.twitterTableRepo = twitterTableRepo;
    }

    public TwitterFriendsTableRepo friends() {
        return twitterFriendsTableRepo;
    }

    public TwitterPostsTableRepo posts() {
        return twitterPostsTableRepo;
    }

    public TwitterTableRepo profiles() {
        return twitterTableRepo;
    }

}
