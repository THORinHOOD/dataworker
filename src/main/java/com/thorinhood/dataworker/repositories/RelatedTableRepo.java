package com.thorinhood.dataworker.repositories;

import com.thorinhood.dataworker.tables.RelatedTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RelatedTableRepo extends CassandraRepository<RelatedTable, UUID> {

    RelatedTable findByTwitter(String twitter);
    RelatedTable findByVkId(String vk);
    RelatedTable findByVkDomain(String vk);
    RelatedTable findByFacebook(String facebook);
    RelatedTable findByInstagram(String instagram);

}
