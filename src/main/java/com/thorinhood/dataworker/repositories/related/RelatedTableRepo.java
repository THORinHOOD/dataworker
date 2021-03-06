package com.thorinhood.dataworker.repositories.related;

import com.thorinhood.dataworker.tables.related.RelatedTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
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
    List<RelatedTable> findAllByVkDomain(String vkDomain);
    List<RelatedTable> findAllByTwitter(String twitter);
    List<RelatedTable> findAllByVkDomainAndTwitter(String vkDomain, String twitter);

}
