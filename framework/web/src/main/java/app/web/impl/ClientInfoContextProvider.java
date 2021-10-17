package app.web.impl;


import app.App;
import app.web.ClientInfo;
import app.web.WebOptions;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.HttpHeaders;
import java.util.Map;
import java.util.UUID;

/**
 * @author chi
 */
public class ClientInfoContextProvider implements Provider<ClientInfo> {
    @Inject
    WebOptions webOptions;

    @Inject
    HttpHeaders headers;

    @Inject
    ContainerRequestContext context;

    @Inject
    App app;

    @Override
    public ClientInfo get() {
        ClientInfo clientInfo = (ClientInfo) context.getProperty("__client_info");
        if (clientInfo == null) {
            String clientId;
            Map<String, Cookie> cookies = headers.getCookies();
            if (cookies.containsKey(webOptions.cookie.clientId)) {
                clientId = cookies.get(webOptions.cookie.clientId).getValue();
            } else {
                clientId = UUID.randomUUID().toString();
            }
            String language;
            if (cookies.containsKey(webOptions.cookie.language)) {
                language = cookies.get(webOptions.cookie.language).getValue();
            } else {
                language = app.language();
            }
            if (!app.supportLanguages().contains(language)) {
                language = app.language();
            }
            clientInfo = new ClientInfoImpl(clientId, language, context.getHeaderString("X-Client-IP"));
            context.setProperty("__client_info", clientInfo);
        }
        return clientInfo;
    }
}
