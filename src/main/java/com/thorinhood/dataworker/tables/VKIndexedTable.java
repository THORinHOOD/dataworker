package com.thorinhood.dataworker.tables;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;

@Table("vk_indexed")
public class VKIndexedTable {

    @PrimaryKey
    private Integer id;

    @Column("last_update")
    private Date lastUpdate;

    public Integer getId() {
        return id;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public VKIndexedTable setId(Integer id) {
        this.id = id;
        return this;
    }

    public VKIndexedTable setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }
}
