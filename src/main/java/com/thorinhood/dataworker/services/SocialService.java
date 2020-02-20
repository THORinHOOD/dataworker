package com.thorinhood.dataworker.services;

import java.util.Collection;
import java.util.List;

public interface SocialService<TYPE> {

    Collection<TYPE> getDefaultUsersInfo(Collection<String> ids);

}
