package com.thorinhood.dataworker.repositories.posts;

import com.thorinhood.dataworker.tables.posts.VKPostsTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VKPostsTableRepo extends CassandraRepository<VKPostsTable, Long> {
    List<VKPostsTable> findAllByProfileId(String profileId);
}
