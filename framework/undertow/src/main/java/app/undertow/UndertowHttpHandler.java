package app.undertow;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import jakarta.ws.rs.core.SecurityContext;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.server.ContainerRequest;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chi
 */
public class UndertowHttpHandler implements HttpHandler {
    private final UndertowHttpContainer container;

    public UndertowHttpHandler(UndertowHttpContainer container) {
        this.container = container;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        try {
            ContainerRequest request = createContainerRequest(exchange);
            request.setWriter(new UndertowResponseWriter(exchange, container));
            container.getApplicationHandler().handle(request);
        } catch (Throwable e) {
            if (exchange.isResponseChannelAvailable()) {
                exchange.setStatusCode(500);
                exchange.getResponseHeaders().add(new HttpString("Content-Type"), "text/plain");
                exchange.getResponseSender().send(e.getMessage());
            }
        }
    }

    private ContainerRequest createContainerRequest(HttpServerExchange exchange) {
        ContainerRequest requestContext = new ContainerRequest(
            baseURI(exchange), requestURI(exchange), exchange.getRequestMethod().toString(), getSecurityContext(),
            new PropertiesDelegate() {
                private final Map<String, Object> properties = new HashMap<>();

                @Override
                public Object getProperty(String name) {
                    return properties.get(name);
                }

                @Override
                public Collection<String> getPropertyNames() {
                    return properties.keySet();
                }

                @Override
                public void setProperty(String name, Object object) {
                    properties.put(name, object);
                }

                @Override
                public void removeProperty(String name) {
                    properties.remove(name);
                }
            });

        RequestBodyParser.RequestBody body = exchange.getAttachment(RequestBodyParser.REQUEST_BODY);
        if (body != null) {
            requestContext.setEntityStream(new ByteArrayInputStream(body.body()));
        }

        exchange.getRequestHeaders().forEach(header -> {
            requestContext.headers(header.getHeaderName().toString(), Lists.newArrayList(header));
        });

        requestContext.header("X-Client-IP", clientIP(exchange));
        return requestContext;
    }

    private URI baseURI(HttpServerExchange exchange) {
        String scheme = scheme(exchange);
        int port = exchange.getHostPort();
        StringBuilder b = new StringBuilder();
        b.append(scheme)
            .append("://")
            .append(exchange.getHostName());
        if (port != 80 && port != 443) {
            b.append(':').append(port);
        }
        b.append('/');
        return URI.create(b.toString());
    }

    private URI requestURI(HttpServerExchange exchange) {
        StringBuilder b = new StringBuilder();
        String requestURI = exchange.getRequestURI();
        if (requestURI.startsWith("/")) {
            b.append(requestURI, 1, requestURI.length());
        } else {
            b.append(requestURI);
        }
        String queryString = exchange.getQueryString();
        if (!Strings.isNullOrEmpty(queryString)) b.append('?').append(queryString);
        return baseURI(exchange).resolve(b.toString());
    }

    private String scheme(HttpServerExchange exchange) {
        String scheme = exchange.getRequestHeaders().getFirst("X-Forwarded-Proto");
        return scheme == null ? exchange.getRequestScheme() : scheme;
    }

    private String clientIP(HttpServerExchange exchange) {
        String xForwardedFor = exchange.getRequestHeaders().getFirst(Headers.X_FORWARDED_FOR_STRING);

        if (Strings.isNullOrEmpty(xForwardedFor)) {
            return exchange.getSourceAddress().getAddress().getHostAddress();
        }
        int index = xForwardedFor.indexOf(',');
        if (index > 0)
            return xForwardedFor.substring(0, index);
        return xForwardedFor;
    }

    private SecurityContext getSecurityContext() {
        return new SecurityContext() {
            @Override
            public boolean isUserInRole(final String role) {
                return false;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public Principal getUserPrincipal() {
                return null;
            }

            @Override
            public String getAuthenticationScheme() {
                return null;
            }
        };
    }
}
