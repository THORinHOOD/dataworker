package com.thorinhood.dataworker.repositories;

import com.thorinhood.dataworker.tables.VKTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VKTableRepo extends CassandraRepository<VKTable, Long> {
}
