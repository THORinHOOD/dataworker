package com.thorinhood.dataworker.repositories;

import com.thorinhood.dataworker.tables.TwitterFriendsPrimaryKey;
import com.thorinhood.dataworker.tables.TwitterFriendsTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TwitterFriendsTableRepo extends CassandraRepository<TwitterFriendsTable, TwitterFriendsPrimaryKey> {
}
