package com.thorinhood.dataworker.repositories;

import com.thorinhood.dataworker.tables.VKIndexedTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VKIndexTableRepo extends CassandraRepository<VKIndexedTable, Long> {

    @Query(value="SELECT id FROM vk_indexed WHERE indexed = false", allowFiltering = true)
    List<Integer> getUnindexed();

}
