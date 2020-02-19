package com.thorinhood.dataworker.tables;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;

@Table("vk_indexed")
public class VKIndexTable {

    @PrimaryKey
    private Integer id;

    @Column("friends_ids_found")
    private Boolean friendsIdsFound;

    @Column
    private Boolean indexed;

    @Column("last_update")
    private Date lastUpdate;

    public Integer getId() {
        return id;
    }

    public Boolean getFriendsIdsFound() {
        return friendsIdsFound;
    }

    public Boolean getIndexed() {
        return indexed;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public VKIndexTable setId(Integer id) {
        this.id = id;
        return this;
    }

    public VKIndexTable setFriendsIdsFound(Boolean friendsIdsFound) {
        this.friendsIdsFound = friendsIdsFound;
        return this;
    }

    public VKIndexTable setIndexed(Boolean indexed) {
        this.indexed = indexed;
        return this;
    }

    public VKIndexTable setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }
}
