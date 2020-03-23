package com.thorinhood.dataworker.tables.friends;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("vk_friends")
public class VKFriendsTable implements FriendsPair {

    @PrimaryKey
    private FriendsPrimaryKey key;

    public String getFirst() {
        return key.getFirst();
    }

    public String getSecond() {
        return key.getSecond();
    }

    public VKFriendsTable setKey(String first, String second) {
        key = new FriendsPrimaryKey()
            .setFirst(first)
            .setSecond(second);
        return this;
    }

    public FriendsPrimaryKey getKey() {
        return key;
    }

}
