package com.thorinhood.dataworker.repositories;

import com.thorinhood.dataworker.tables.TwitterTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TwitterTableRepo extends CassandraRepository<TwitterTable, String> {
}
