package com.thorinhood.dataworker.services.db;

import com.thorinhood.dataworker.tables.HasId;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class DBService<TABLEREPO extends CassandraRepository<TABLE, ID>,
                                UNINDEXEDREPO extends CassandraRepository<UNTABLE, UNID>,
                                TABLE extends HasId<ID>,
                                UNTABLE extends HasId<UNID>,
                                ID, UNID> {

    protected final TABLEREPO tableRepo;
    protected final UNINDEXEDREPO unindexedRepo;
    protected final CassandraTemplate cassandraTemplate;
    protected final String unindexedTable;
    protected final String needFriendsTable;
    protected final Class<ID> idClass;
    protected final Class<UNID> unidClass;

    public DBService(TABLEREPO tableRepo,
                     UNINDEXEDREPO unindexedRepo,
                     CassandraTemplate cassandraTemplate,
                     String unindexedTable,
                     String needFriendsTable,
                     Class<ID> idClass,
                     Class<UNID> unidClass) {
        this.cassandraTemplate = cassandraTemplate;
        this.tableRepo = tableRepo;
        this.unindexedTable = unindexedTable;
        this.unindexedRepo = unindexedRepo;
        this.needFriendsTable = needFriendsTable;
        this.idClass = idClass;
        this.unidClass = unidClass;
    }

    public List<UNID> getAllUnindexedPages() {
        return cassandraTemplate.getCqlOperations().queryForList("SELECT id FROM " + unindexedTable, unidClass);
    }

    public void savePages(Collection<TABLE> tables) {
        tableRepo.saveAll(tables.stream()
                .filter(table -> !tableRepo.existsById(table.id()))
                .collect(Collectors.toList()));
        String deleteUnindexed = "DELETE FROM " + unindexedTable + " WHERE id = ?";
       // String insertNeedFriends = "INSERT INTO " + needFriendsTable + " (id) VALUES (?)";
        tables.forEach(table -> {
            cassandraTemplate.getCqlOperations().execute(deleteUnindexed, table.id());
         //   cassandraTemplate.getCqlOperations().execute(insertNeedFriends, table.id());
        });
    }

    public void saveUnindexed(Collection<UNTABLE> ids) {
        unindexedRepo.saveAll(ids.stream()
                .filter(untable -> !unindexedRepo.existsById(untable.id()))
                .collect(Collectors.toList()));
    }

    public Optional<TABLE> getPageById(ID id) {
        return tableRepo.findById(id);
    }

    public boolean containsPage(ID id) {
        return tableRepo.existsById(id);
    }

}
