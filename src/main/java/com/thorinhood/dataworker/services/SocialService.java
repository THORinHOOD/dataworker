package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.tables.Profile;
import com.thorinhood.dataworker.utils.common.BatchProfiles;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public interface SocialService<TABLE extends Profile<ID>, ID> {

    void getDefaultUsersInfo(Collection<ID> ids, BlockingQueue<BatchProfiles<TABLE, ID>> queue);

}
