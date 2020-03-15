package com.thorinhood.dataworker.tables;

import java.util.Collection;
import java.util.List;

public interface Profile<ID, FRIENDS_TABLE extends FriendsPair> extends HasId<ID>, HasPagesLinks {

    Collection<ID> getLinked();
    List<FRIENDS_TABLE> generatePairs();
    String getId();

}
