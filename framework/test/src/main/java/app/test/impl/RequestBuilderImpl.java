package app.test.impl;

import app.ApplicationException;
import app.test.RequestBuilder;
import app.util.JSON;
import com.google.common.collect.Maps;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.spi.Container;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chi
 */
public class RequestBuilderImpl implements RequestBuilder {
    private final String httpMethod;
    private final URI requestURI;
    private final Container container;
    private final ApplicationHandler applicationHandler;

    private final Map<String, String> headers = Maps.newHashMap();
    private final Map<String, String> cookies = Maps.newHashMap();

    private URI baseURL;
    private Object entity;
    private String contentType;
    private String accept;

    Throwable error;
    ContainerResponse response;

    public RequestBuilderImpl(String httpMethod, URI requestURI, Container container) {

        this.httpMethod = httpMethod;
        this.requestURI = requestURI;
        this.container = container;
        this.applicationHandler = container.getApplicationHandler();
    }

    @Override
    public RequestBuilder setBaseURL(String baseURL) {
        this.baseURL = URI.create(baseURL);
        return this;
    }

    @Override
    public RequestBuilder setAccept(String accept) {
        this.accept = accept;
        return this;
    }

    @Override
    public RequestBuilder setEntity(Object entity) {
        setEntity(entity, MediaType.APPLICATION_JSON);
        setAccept(MediaType.APPLICATION_JSON);
        return this;
    }

    @Override
    public RequestBuilder setEntity(Object entity, String contentType) {
        this.entity = entity;
        this.contentType = contentType;
        return this;
    }

    @Override
    public RequestBuilder setHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    @Override
    public RequestBuilder setCookie(String name, String value) {
        cookies.put(name, value);
        return this;
    }

    @Override
    public ContainerResponse execute() {
        ContainerRequest request = new ContainerRequest(baseURL == null ? URI.create("http://localhost:8080/") : baseURL, requestURI, httpMethod, getSecurityContext(), getProperties());
        if (entity != null) {
            request.setEntityStream(new ByteArrayInputStream(JSON.toJSONBytes(entity)));
        }
        MultivaluedMap<String, String> requestHeaders = request.getRequestHeaders();
        headers.forEach(requestHeaders::add);
        requestHeaders.add("Cookie", cookieValue());
        if (contentType != null) {
            requestHeaders.add("Content-Type", contentType);
        }
        if (accept != null) {
            requestHeaders.add("Accept", accept);
        }

        request.setWriter(new MockContainerResponseWriter(this, container));
        applicationHandler.handle(request);

        if (error != null) {
            if (error instanceof RuntimeException) {
                throw (RuntimeException) error;
            }
            throw new ApplicationException(error);
        }
        return response;
    }

    private String cookieValue() {
        StringBuilder b = new StringBuilder();
        cookies.forEach((s, s2) -> b.append(s).append('=').append(s2).append(";"));
        return b.toString();
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

    private PropertiesDelegate getProperties() {
        return new PropertiesDelegate() {
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
        };
    }

}
