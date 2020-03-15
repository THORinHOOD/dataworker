package com.thorinhood.dataworker.tables;

import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Table("twitter")
public class TwitterTable implements Profile<String, TwitterFriendsTable> {

    @PrimaryKey
    private String screenName;

    @Column
    private String createdDate;

    @Column
    private String description;

    @Column
    private Integer followersCount;

    @Column
    private Integer friendsCount;

    @Column
    private String language;

    @Column
    private String location;

    @Column
    private String name;

    @Column
    private String profileImageUrl;

    @Transient
    private List<String> friends;

    public TwitterTable setFriends(List<String> friends) {
        this.friends = friends;
        return this;
    }

    public TwitterTable setScreenName(String screenName) {
        this.screenName = screenName;
        return this;
    }

    public TwitterTable setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public TwitterTable setDescription(String description) {
        this.description = description;
        return this;
    }

    public TwitterTable setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
        return this;
    }

    public TwitterTable setFriendsCount(Integer friendsCount) {
        this.friendsCount = friendsCount;
        return this;
    }

    public TwitterTable setLanguage(String language) {
        this.language = language;
        return this;
    }

    public TwitterTable setLocation(String location) {
        this.location = location;
        return this;
    }

    public TwitterTable setName(String name) {
        this.name = name;
        return this;
    }

    public TwitterTable setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        return this;
    }

    public List<String> getFriends() {
        return friends;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getDescription() {
        return description;
    }

    public Integer getFollowersCount() {
        return followersCount;
    }

    public Integer getFriendsCount() {
        return friendsCount;
    }

    public String getLanguage() {
        return language;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    @Override
    public String id() {
        return getScreenName();
    }

    @Override
    public String vkId() {
        return null;
    }

    @Override
    public String vkDomain() {
        return null;
    }

    @Override
    public String twitter() {
        return null;
    }

    @Override
    public String instagram() {
        return null;
    }

    @Override
    public String facebook() {
        return null;
    }

    @Override
    public Collection<String> getLinked() {
        return friends;
    }

    @Override
    public List<TwitterFriendsTable> generatePairs() {
        if (CollectionUtils.isEmpty(friends)) {
            return Collections.emptyList();
        }
        return friends.stream()
                .map(id -> new TwitterFriendsTable().setKey(getScreenName(), id))
                .collect(Collectors.toList());
    }

    @Override
    public String getId() {
        return screenName;
    }

}
