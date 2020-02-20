package com.thorinhood.dataworker.tables;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("twitter")
public class TwitterTable implements HasId<String> {

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

}
