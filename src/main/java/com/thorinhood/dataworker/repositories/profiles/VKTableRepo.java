package com.thorinhood.dataworker.repositories.profiles;

import com.thorinhood.dataworker.tables.profile.VKTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VKTableRepo extends CassandraRepository<VKTable, String> {
    boolean existsByDomain(String domain);
}
