package com.thorinhood.dataworker.utils;

import com.thorinhood.dataworker.repositories.related.RelatedTableRepo;
import com.thorinhood.dataworker.tables.related.RelatedTable;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Finder {

    public static RelatedTable findByLongValue(RelatedTableRepo repo, RelatedTable relatedTable,
                                               Function<RelatedTable, Long> extractor,
                                               BiFunction<RelatedTableRepo, Long, RelatedTable> finder) {
        Long value = extractor.apply(relatedTable);
        return value != null ? finder.apply(repo, value) : null;
    }

    public static RelatedTable findByStringValue(RelatedTableRepo repo, RelatedTable relatedTable,
                                    Function<RelatedTable, String> extractor,
                                    BiFunction<RelatedTableRepo, String, RelatedTable> finder) {
        String value = extractor.apply(relatedTable);
        return value != null && !value.equalsIgnoreCase("null") ? finder.apply(repo, value) : null;
    }

}
