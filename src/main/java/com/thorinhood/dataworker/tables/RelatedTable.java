package com.thorinhood.dataworker.tables;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("related_profiles")
public class RelatedTable {

    @Id
    private UUID uid;
    @Column("vk_domain")
    private String vkDomain;
    @Column("vk_id")
    private Long vkId;
    private String twitter;
    private String facebook;
    private String instagram;

    public UUID getUid() {
        return uid;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public RelatedTable setUid(UUID uid) {
        this.uid = uid;
        return this;
    }

    public String getVkDomain() {
        return vkDomain;
    }

    public Long getVkId() {
        return vkId;
    }

    public RelatedTable setVkDomain(String vkDomain) {
        this.vkDomain = vkDomain;
        return this;
    }

    public RelatedTable setVkId(Long vkId) {
        this.vkId = vkId;
        return this;
    }

    public RelatedTable setTwitter(String twitter) {
        this.twitter = twitter;
        return this;
    }

    public RelatedTable setFacebook(String facebook) {
        this.facebook = facebook;
        return this;
    }

    public RelatedTable setInstagram(String instagram) {
        this.instagram = instagram;
        return this;
    }
}
