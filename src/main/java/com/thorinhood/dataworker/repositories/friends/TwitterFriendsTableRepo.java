package com.thorinhood.dataworker.repositories.friends;

import com.thorinhood.dataworker.tables.friends.FriendsPrimaryKey;
import com.thorinhood.dataworker.tables.friends.TwitterFriendsTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TwitterFriendsTableRepo extends CassandraRepository<TwitterFriendsTable, FriendsPrimaryKey> {

    @Query("SELECT * FROM twitter_friends WHERE first = :first")
    List<TwitterFriendsTable> searchByFirst(@Param("first") String first);

    @Query("SELECT * FROM twitter_friends WHERE second = :second")
    List<TwitterFriendsTable> searchBySecond(@Param("second") String second);

}
