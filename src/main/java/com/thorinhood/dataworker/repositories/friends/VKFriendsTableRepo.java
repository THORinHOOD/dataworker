package com.thorinhood.dataworker.repositories.friends;

import com.thorinhood.dataworker.tables.friends.FriendsPrimaryKey;
import com.thorinhood.dataworker.tables.friends.VKFriendsTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VKFriendsTableRepo extends CassandraRepository<VKFriendsTable, FriendsPrimaryKey> {

    @Query("SELECT * FROM vk_friends WHERE first = :first")
    List<VKFriendsTable> searchByFirst(@Param("first") String first);

    @Query("SELECT * FROM vk_friends WHERE second = :second")
    List<VKFriendsTable> searchBySecond(@Param("second") String second);

}
