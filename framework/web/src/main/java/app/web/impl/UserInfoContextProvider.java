package app.web.impl;

import app.web.UserInfo;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;

/**
 * @author chi
 */
@Priority(Priorities.USER)
public class UserInfoContextProvider implements Provider<UserInfo> {
    @Inject
    ContainerRequestContext context;

    @Override
    public UserInfo get() {
        return new UserInfoImpl(context.getHeaderString("X-Client-IP"));
    }
}
