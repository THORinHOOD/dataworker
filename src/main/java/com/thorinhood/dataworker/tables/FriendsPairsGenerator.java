package com.thorinhood.dataworker.tables;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FriendsPairsGenerator {

    public static List<VKFriendsTable> generate(Collection<VKTable> profiles) {
        return profiles.stream().flatMap(profile -> profile.getFriends().stream()
                .map(id -> new VKFriendsTable().setKey(profile.getId(), id)))
                .collect(Collectors.toList());
    }

}
