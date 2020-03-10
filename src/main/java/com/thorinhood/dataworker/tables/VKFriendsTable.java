package com.thorinhood.dataworker.tables;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("vk_friends")
public class VKFriendsTable implements FriendsPair {

    @PrimaryKey
    private VKFriendsPrimaryKey key;

    public String getFirst() {
        return key.getFirst();
    }

    public String getSecond() {
        return key.getSecond();
    }

    public VKFriendsTable setKey(String first, String second) {
        key = new VKFriendsPrimaryKey()
                .setFirst(first)
                .setSecond(second);
        return this;
    }

    public VKFriendsPrimaryKey getKey() {
        return key;
    }

}
