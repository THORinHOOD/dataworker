package com.thorinhood.dataworker.repositories;

import com.thorinhood.dataworker.tables.MyColumns;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyColumnsRepo extends CassandraRepository<MyColumns, Integer> {
}
