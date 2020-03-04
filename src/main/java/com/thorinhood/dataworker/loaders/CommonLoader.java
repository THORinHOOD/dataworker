package com.thorinhood.dataworker.loaders;

import com.thorinhood.dataworker.repositories.RelatedTableRepo;
import com.thorinhood.dataworker.services.SocialService;
import com.thorinhood.dataworker.services.db.DBService;
import com.thorinhood.dataworker.tables.HasId;
import com.thorinhood.dataworker.tables.HasPagesLinks;
import com.thorinhood.dataworker.tables.RelatedTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public abstract class CommonLoader<DB extends DBService<TABLEREPO, UNTABLEREPO, TABLE, UNTABLE, ID, UNID>,
                                   TABLEREPO extends CassandraRepository<TABLE, ID>,
                                   UNTABLEREPO extends CassandraRepository<UNTABLE, UNID>,
                                   TABLE extends HasId<ID> & HasPagesLinks,
                                   UNTABLE extends HasId<UNID>,
                                   ID, UNID> {

    protected final DB dbService;
    protected final SocialService<TABLE> service;

    public CommonLoader(DB dbService, SocialService<TABLE> service) {
        this.dbService = dbService;
        this.service = service;
    }

    public void loadData(BiFunction<RelatedTableRepo, RelatedTable, RelatedTable> getFoundBy) {
        List<UNID> unindexedPages = dbService.getAllUnindexedPages();
        if (!CollectionUtils.isEmpty(unindexedPages)) {
            Collection<TABLE> users = service.getDefaultUsersInfo(unindexedPages.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList()));
            dbService.savePages(users, getFoundBy);
        }
    }

}
