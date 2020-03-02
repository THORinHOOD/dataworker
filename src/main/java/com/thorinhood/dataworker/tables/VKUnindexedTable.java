package com.thorinhood.dataworker.tables;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("vk_unindexed")
public class VKUnindexedTable implements HasId<String> {

    @PrimaryKey
    private String id;

    public VKUnindexedTable(String id) {
        this.id = id;
    }

    public VKUnindexedTable setId(String id) {
        this.id = id;
        return this;
    }

    public String getId() {
        return id;
    }

    @Override
    public String id() {
        return getId();
    }

}
