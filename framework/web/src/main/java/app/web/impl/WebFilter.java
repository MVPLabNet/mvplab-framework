package app.web.impl;

import app.App;
import app.web.ClientInfo;
import app.web.Cookies;
import app.web.WebOptions;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * @author chi
 */
public class WebFilter implements ContainerResponseFilter {
    @Inject
    WebOptions webOptions;
    @Inject
    SessionRepository sessionRepository;
    @Inject
    App app;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        Map<String, Cookie> cookies = requestContext.getCookies();
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        final String domain = requestContext.getUriInfo().getBaseUri().getHost();

        if (!cookies.containsKey(webOptions.cookie.clientId) && isCookieEnabled(domain)) {
            ClientInfo clientInfo = (ClientInfo) requestContext.getProperty("__client_info");
            String clientId = clientInfo == null ? UUID.randomUUID().toString() : clientInfo.id();
            String language = clientInfo == null ? app.language() : clientInfo.language();
            headers.add("Set-Cookie", new NewCookie(webOptions.cookie.clientId, clientId, "/", webOptions.cookie.baseDomain, null, Integer.MAX_VALUE, false));
            headers.add("Set-Cookie", new NewCookie(webOptions.cookie.language, language, "/", webOptions.cookie.baseDomain, null, Integer.MAX_VALUE, false));
        }

        SessionInfoImpl sessionInfo = (SessionInfoImpl) requestContext.getProperty("__session_info");
        if (sessionInfo == null) {
            return;
        }

        if (!cookies.containsKey(webOptions.cookie.sessionId) && isCookieEnabled(domain)) {
            headers.add("Set-Cookie", Cookies.newSessionCookie(webOptions.cookie.sessionId, sessionInfo.id(), webOptions.cookie.baseDomain));
        }

        if (sessionInfo.invalidated) {
            headers.add("Set-Cookie", Cookies.removeCookie(webOptions.cookie.sessionId, webOptions.cookie.baseDomain));
            sessionRepository.remove(sessionInfo.id());
        } else if (sessionInfo.changed) {
            sessionRepository.update(sessionInfo.id(), sessionInfo.data);
        }
    }

    boolean isCookieEnabled(String domain) {
        if (webOptions.cookie.domains != null && webOptions.cookie.domains.containsKey(domain)) {
            return webOptions.cookie.domains.get(domain);
        }
        return false;
    }

}
