package com.thorinhood.dataworker.repositories;

import com.thorinhood.dataworker.tables.VKFriendsPrimaryKey;
import com.thorinhood.dataworker.tables.VKFriendsTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VKFriendsTableRepo extends CassandraRepository<VKFriendsTable, VKFriendsPrimaryKey> {
}
