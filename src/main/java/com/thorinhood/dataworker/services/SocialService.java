package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.tables.friends.FriendsPair;
import com.thorinhood.dataworker.tables.profile.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class SocialService<
        TABLE extends Profile<ID, FRIENDS_TABLE>,
        ID,
        FRIENDS_TABLE extends FriendsPair> {

    protected final Logger logger;

    protected SocialService(Class serviceClass) {
        logger = LoggerFactory.getLogger(serviceClass);
    }

    public abstract List<TABLE> getUsersInfo(List<ID> ids);

}
