package com.thorinhood.dataworker.tables.posts;

import com.thorinhood.dataworker.tables.HasId;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("twitter_posts")
public class TwitterPostsTable implements HasId<Long> {

    @PrimaryKey
    private Long id;

    @Column("profile_id")
    private String profileId;

    @Column("text")
    private String text;

    public Long getId() {
        return id;
    }

    public String getProfileId() {
        return profileId;
    }

    public String getText() {
        return text;
    }

    public TwitterPostsTable setId(Long id) {
        this.id = id;
        return this;
    }

    public TwitterPostsTable setProfileId(String profileId) {
        this.profileId = profileId;
        return this;
    }

    public TwitterPostsTable setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public Long id() {
        return getId();
    }
}
