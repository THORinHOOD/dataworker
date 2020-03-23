package com.thorinhood.dataworker.repositories.profiles;

import com.thorinhood.dataworker.tables.profile.TwitterTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TwitterTableRepo extends CassandraRepository<TwitterTable, String> {
}
