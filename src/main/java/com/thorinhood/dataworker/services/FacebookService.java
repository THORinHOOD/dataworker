package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.utils.common.PersonInfo;
import com.thorinhood.dataworker.utils.common.SocialService;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FacebookService implements SocialService<PersonInfo> {

//    private final Facebook facebook;

    public FacebookService() {
            //Facebook facebook) {
//        this.facebook = facebook;
    }

    @Override
    public List<PersonInfo> getDefaultUsersInfo(Collection<String> ids) {
//        ArrayList<User> users = new ArrayList<>();
//        String [] fields = {"id", "name"};
//        ids.forEach(id -> users.add(facebook.fetchObject(id, User.class, fields)));
        return null;
    }

}
