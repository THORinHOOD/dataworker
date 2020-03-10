package com.thorinhood.dataworker.tables;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;

@PrimaryKeyClass
public class VKFriendsPrimaryKey implements Serializable {

    @PrimaryKeyColumn(name = "first", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String first;

    @PrimaryKeyColumn(name = "second", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String second;

    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

    public VKFriendsPrimaryKey setFirst(String first) {
        this.first = first;
        return this;
    }

    public VKFriendsPrimaryKey setSecond(String second) {
        this.second = second;
        return this;
    }

}