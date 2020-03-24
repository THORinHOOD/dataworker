package com.thorinhood.dataworker.repositories.posts;

import com.thorinhood.dataworker.tables.posts.TwitterPostsTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TwitterPostsTableRepo extends CassandraRepository<TwitterPostsTable, Long> {
    List<TwitterPostsTable> findAllByProfileId(String profileId);
}
