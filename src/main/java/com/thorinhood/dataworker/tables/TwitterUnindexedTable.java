package com.thorinhood.dataworker.tables;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("twitter_unindexed")
public class TwitterUnindexedTable implements HasId<String> {

    @PrimaryKey
    private String id;

    public TwitterUnindexedTable(String id) {
        this.id = id;
    }

    public TwitterUnindexedTable setId(String id) {
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
