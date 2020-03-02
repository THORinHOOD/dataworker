package com.thorinhood.dataworker.repositories;

import com.thorinhood.dataworker.tables.TwitterUnindexedTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TwitterUnindexedTableRepo extends CassandraRepository<TwitterUnindexedTable, String> {
}
