package app.web.impl;

import app.web.RequestInfo;
import com.google.common.collect.Maps;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.UriInfo;
import java.util.Map;
import java.util.Optional;

/**
 * @author chi
 */
public class RequestInfoContextProvider implements Provider<RequestInfo> {
    @Inject
    ContainerRequestContext context;
    @Inject
    UriInfo uriInfo;

    @Override
    public RequestInfo get() {
        return new RequestInfoImpl(context, uriInfo);
    }

    static class RequestInfoImpl implements RequestInfo {
        private final ContainerRequestContext requestContext;
        private final UriInfo uriInfo;

        RequestInfoImpl(ContainerRequestContext requestContext, UriInfo uriInfo) {
            this.requestContext = requestContext;
            this.uriInfo = uriInfo;
        }

        @Override
        public String path() {
            return requestContext.getUriInfo().getPath();
        }

        @Override
        public String ip() {
            return requestContext.getHeaderString("X-Client-IP");
        }

        @Override
        public String method() {
            return requestContext.getMethod();
        }

        @Override
        public Optional<String> queryParam(String name) {
            return Optional.ofNullable(uriInfo.getQueryParameters(true).getFirst(name));
        }

        @Override
        public String pathParam(String name) {
            return requestContext.getUriInfo().getPathParameters().getFirst(name);
        }

        @Override
        public Map<String, String> headers() {
            Map<String, String> headers = Maps.newHashMap();
            requestContext.getHeaders().forEach((name, values) -> headers.put(name, values == null || values.isEmpty() ? null : values.get(0)));
            return headers;
        }

        @Override
        public Map<String, String> cookies() {
            Map<String, String> cookies = Maps.newHashMap();
            requestContext.getCookies().forEach((s, cookie) -> cookies.put(s, cookie.getValue()));
            return cookies;
        }

        @Override
        public String uri() {
            return requestContext.getUriInfo().getRequestUri().toString();
        }
    }
}
