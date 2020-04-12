package com.thorinhood.dataworker.repositories.profiles;

import com.thorinhood.dataworker.tables.profile.TwitterTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TwitterTableRepo extends CassandraRepository<TwitterTable, String> {
    TwitterTable findByScreenName(String screenName);
    List<TwitterTable> findAllByVk(String vk);
    boolean existsByScreenName(String screenName);
}
