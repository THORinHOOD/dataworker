package com.thorinhood.dataworker.repositories;

import com.thorinhood.dataworker.tables.VKUnindexedTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VKUnindexedTableRepo extends CassandraRepository<VKUnindexedTable, String> {
}
