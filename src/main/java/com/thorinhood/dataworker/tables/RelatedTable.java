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
    private String vkDomain = "null";
    @Column("vk_id")
    private String vkId = "null";
    private String twitter = "null";
    private String facebook = "null";
    private String instagram = "null";

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

    public String getVkId() {
        return vkId;
    }

    public RelatedTable setVkDomain(String vkDomain) {
        if (vkDomain == null) {
            return this;
        }
        this.vkDomain = vkDomain;
        return this;
    }

    public RelatedTable setVkId(String vkId) {
        if (vkDomain == null) {
            return this;
        }
        this.vkId = vkId;
        return this;
    }

    public RelatedTable setTwitter(String twitter) {
        if (vkDomain == null) {
            return this;
        }
        this.twitter = twitter;
        return this;
    }

    public RelatedTable setFacebook(String facebook) {
        if (vkDomain == null) {
            return this;
        }
        this.facebook = facebook;
        return this;
    }

    public RelatedTable setInstagram(String instagram) {
        if (vkDomain == null) {
            return this;
        }
        this.instagram = instagram;
        return this;
    }
}
