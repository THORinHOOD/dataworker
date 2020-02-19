package com.thorinhood.dataworker.tables;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("vk_unindexed")
public class VKUnindexedTable {

    @PrimaryKey
    private Integer id;

    public VKUnindexedTable(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
