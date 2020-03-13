package com.thorinhood.dataworker.tables;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("twitter_friends")
public class TwitterFriendsTable implements FriendsPair {

    @PrimaryKey
    private TwitterFriendsPrimaryKey key;

    public String getFirst() {
        return key.getFirst();
    }

    public String getSecond() {
        return key.getSecond();
    }

    public TwitterFriendsTable setKey(String first, String second) {
        key = new TwitterFriendsPrimaryKey()
                .setFirst(first)
                .setSecond(second);
        return this;
    }

    public TwitterFriendsPrimaryKey getKey() {
        return key;
    }

}
