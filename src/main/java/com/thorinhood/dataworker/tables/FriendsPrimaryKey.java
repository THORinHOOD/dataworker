package com.thorinhood.dataworker.tables;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.Objects;

@PrimaryKeyClass
public class FriendsPrimaryKey implements Serializable {

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

    public FriendsPrimaryKey setFirst(String first) {
        this.first = first;
        return this;
    }

    public FriendsPrimaryKey setSecond(String second) {
        this.second = second;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendsPrimaryKey that = (FriendsPrimaryKey) o;
        return (Objects.equals(first, that.first) && Objects.equals(second, that.second)) ||
               (Objects.equals(first, that.second) && Objects.equals(second, this.first));
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

}
