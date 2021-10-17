package app.web.impl;

import app.web.UserInfo;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

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
