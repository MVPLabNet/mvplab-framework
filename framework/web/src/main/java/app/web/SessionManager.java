package app.web;

import app.web.impl.SessionInfoImpl;
import app.web.impl.SessionRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Cookie;
import java.util.UUID;

/**
 * @author chi
 */
public class SessionManager {
    private static final String PROPERTY_SESSION = "__session_info";

    @Inject
    SessionRepository sessionRepository;

    @Inject
    WebOptions webOptions;

    public SessionInfo get(ContainerRequestContext requestContext) {
        SessionInfoImpl session = (SessionInfoImpl) requestContext.getProperty(PROPERTY_SESSION);
        if (session == null) {
            String sessionId = sessionId(requestContext);
            session = new SessionInfoImpl(sessionId, sessionRepository);
            requestContext.setProperty(PROPERTY_SESSION, session);
        }
        return session;
    }

    private String sessionId(ContainerRequestContext requestContext) {
        Cookie sessionId = requestContext.getCookies().get(webOptions.cookie.sessionId);
        if (sessionId != null) {
            return sessionId.getValue();
        }
        return UUID.randomUUID().toString();
    }
}
