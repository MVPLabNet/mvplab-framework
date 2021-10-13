package app.web.impl;


import app.web.SessionInfo;
import app.web.SessionManager;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.container.ContainerRequestContext;

/**
 * @author chi
 */
public class SessionInfoContextProvider implements Provider<SessionInfo> {
    @Inject
    SessionManager sessionManager;

    @Inject
    ContainerRequestContext context;

    @Override
    public SessionInfo get() {
        return sessionManager.get(context);
    }
}
