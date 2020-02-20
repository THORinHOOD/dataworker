package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.utils.common.PersonInfo;

import java.util.Collection;

public class FacebookService implements SocialService<PersonInfo> {

//    private final Facebook facebook;

    public FacebookService() {
            //Facebook facebook) {
//        this.facebook = facebook;
    }

    @Override
    public Collection<PersonInfo> getDefaultUsersInfo(Collection<String> ids) {
//        ArrayList<User> users = new ArrayList<>();
//        String [] fields = {"id", "name"};
//        ids.forEach(id -> users.add(facebook.fetchObject(id, User.class, fields)));
        return null;
    }

}
