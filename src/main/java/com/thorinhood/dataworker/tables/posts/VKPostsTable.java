package com.thorinhood.dataworker.tables.posts;

import com.thorinhood.dataworker.tables.HasId;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("vk_posts")
public class VKPostsTable implements HasId<Long> {

    @PrimaryKey
    private Long id;

    @Column("profile_id")
    private String profileId;

    @Column("text")
    private String text;

    public VKPostsTable setProfileId(String profileId) {
        this.profileId = profileId;
        return this;
    }

    public VKPostsTable setId(Long id) {
        this.id = id;
        return this;
    }

    public VKPostsTable setText(String text) {
        this.text = text;
        return this;
    }

    public String getProfileId() {
        return profileId;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    @Override
    public Long id() {
        return getId();
    }

}
