package com.thorinhood.dataworker.tables;

import java.util.Collection;

public interface Profile<ID> extends HasId<ID>, HasPagesLinks {

    Collection<ID> getLinked();

}
