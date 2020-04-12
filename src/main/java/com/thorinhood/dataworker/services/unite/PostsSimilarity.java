package com.thorinhood.dataworker.services.unite;

import com.thorinhood.dataworker.repositories.TwitterReposBundle;
import com.thorinhood.dataworker.repositories.VkReposBundle;
import com.thorinhood.dataworker.tables.posts.TwitterPostsTable;
import com.thorinhood.dataworker.tables.posts.VKPostsTable;

import java.util.List;
import java.util.stream.Collectors;

public class PostsSimilarity {

    private final CosineService cosineService;
    private final VkReposBundle vkReposBundle;
    private final TwitterReposBundle twitterReposBundle;

    public PostsSimilarity(VkReposBundle vkReposBundle, TwitterReposBundle twitterReposBundle) {
        cosineService = new CosineService();
        this.vkReposBundle = vkReposBundle;
        this.twitterReposBundle = twitterReposBundle;
    }

    public double cachedVKCount(List<String> vkPosts, String twitterId) {
        List<String> twitterPosts = twitterReposBundle.posts().findAllByProfileId(twitterId).stream()
                .map(TwitterPostsTable::getText)
                .collect(Collectors.toList());
        return cosineService.similarity(vkPosts, twitterPosts);
    }

    public double cachedTwitterCount(List<String> twitterPosts, String vkDomain) {
        List<String> vkPosts = vkReposBundle.posts().findAllByProfileId(vkDomain).stream()
                .map(VKPostsTable::getText)
                .collect(Collectors.toList());
        return cosineService.similarity(vkPosts, twitterPosts);
    }

    public double count(String vkDomain, String twitterId) {
        List<String> vkPosts = vkReposBundle.posts().findAllByProfileId(vkDomain).stream()
                .map(VKPostsTable::getText)
                .collect(Collectors.toList());
        List<String> twitterPosts = twitterReposBundle.posts().findAllByProfileId(twitterId).stream()
                .map(TwitterPostsTable::getText)
                .collect(Collectors.toList());
        return cosineService.similarity(vkPosts, twitterPosts);
    }

}
