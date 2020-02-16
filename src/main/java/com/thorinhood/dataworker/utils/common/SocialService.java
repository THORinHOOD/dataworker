package com.thorinhood.dataworker.utils.common;

import java.util.Collection;
import java.util.List;

public interface SocialService<TYPE> {

    List<TYPE> getDefaultUsersInfo(Collection<String> ids);

}
