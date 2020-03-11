package com.thorinhood.dataworker.tables;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("twitter_friends")
public class TwitterFriendsTable implements FriendsPair {

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

    public TwitterFriendsTable setFirst(String first) {
        this.first = first;
        return this;
    }

    public TwitterFriendsTable setSecond(String second) {
        this.second = second;
        return this;
    }

}
