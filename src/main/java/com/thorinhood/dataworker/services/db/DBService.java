package com.thorinhood.dataworker.services.db;

import com.thorinhood.dataworker.tables.HasId;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class DBService<REPO extends CassandraRepository<TABLE, ID>, TABLE extends HasId<ID>, ID> {

    protected final REPO repo;
    protected final CassandraTemplate cassandraTemplate;
    protected final String unindexedTable;
    protected final String needFriendsTable;

    public DBService(REPO repo,
                     CassandraTemplate cassandraTemplate,
                     String unindexedTable,
                     String needFriendsTable) {
        this.cassandraTemplate = cassandraTemplate;
        this.repo = repo;
        this.unindexedTable = unindexedTable;
        this.needFriendsTable = needFriendsTable;
    }

    protected List<ID> getAllUnindexedPages(Class<ID> clazz) {
        return cassandraTemplate.getCqlOperations().queryForList("SELECT id FROM " + unindexedTable, clazz);
    }

    public void savePages(Collection<TABLE> tables) {
        repo.saveAll(tables);
        String deleteUnindexed = "DELETE FROM " + unindexedTable + " WHERE id = ?";
        String insertNeedFriends = "INSERT INTO " + needFriendsTable + " (id) VALUES (?)";
        tables.forEach(table -> {
            cassandraTemplate.getCqlOperations().execute(deleteUnindexed, table.id());
            cassandraTemplate.getCqlOperations().execute(insertNeedFriends, table.id());
        });
    }

    public Optional<TABLE> getPageById(ID id) {
        return repo.findById(id);
    }

    public boolean containsPage(ID id) {
        return repo.existsById(id);
    }

    public abstract List<ID> getAllUnindexedPages();

}
