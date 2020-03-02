package com.thorinhood.dataworker.tables;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("vk_unindexed")
public class VKUnindexedTable implements HasId<Long> {

    @PrimaryKey
    private Long id;

    public VKUnindexedTable(Long id) {
        this.id = id;
    }

    public VKUnindexedTable setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getId() {
        return id;
    }

    @Override
    public Long id() {
        return getId();
    }

}
