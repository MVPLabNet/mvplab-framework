package app.test;

import org.glassfish.jersey.server.ContainerResponse;

/**
 * @author chi
 */
public interface RequestBuilder {
    RequestBuilder setBaseURL(String baseURL);

    RequestBuilder setAccept(String accept);

    RequestBuilder setEntity(Object entity);

    RequestBuilder setEntity(Object entity, String contentType);

    RequestBuilder setHeader(String name, String value);

    RequestBuilder setCookie(String name, String value);

    ContainerResponse execute();
}
