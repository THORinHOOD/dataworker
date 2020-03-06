package com.thorinhood.dataworker.utils.common;

import com.thorinhood.dataworker.tables.Profile;

import java.util.Collection;

public class BatchProfiles<TABLE extends Profile<ID>, ID> {

    private final Collection<TABLE> profiles;
    private final boolean isEnd;

    public static <TABLE2 extends Profile<ID2>, ID2>  BatchProfiles<TABLE2, ID2> end() {
        return new BatchProfiles<>();
    }

    public static <TABLE2 extends Profile<ID2>, ID2>  BatchProfiles<TABLE2, ID2> next(Collection<TABLE2> profiles) {
        return new BatchProfiles<>(profiles);
    }

    private BatchProfiles(Collection<TABLE> profiles) {
        this.profiles = profiles;
        isEnd = false;
    }

    private BatchProfiles() {
        this.profiles = null;
        isEnd = true;
    }

    public Collection<TABLE> getProfiles() {
        return profiles;
    }

    public boolean isEnd() {
        return isEnd;
    }

}
