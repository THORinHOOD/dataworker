package com.thorinhood.dataworker.repositories.profiles;

import com.thorinhood.dataworker.tables.profile.VKTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VKTableRepo extends CassandraRepository<VKTable, String> {
    boolean existsByDomain(String domain);
    VKTable findByDomain(String domain);
    List<VKTable> findAllByTwitter(String twitter);
}
