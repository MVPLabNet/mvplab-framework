package app.web.impl;

import app.resource.Resource;
import com.google.common.io.ByteStreams;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author chi
 */
@Priority(Priorities.ENTITY_CODER - 100)
public class ResourceMessageBodyWriter implements MessageBodyWriter<Object> {
    @Override
    public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Resource.class.isAssignableFrom(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeTo(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        Resource resource = (Resource) o;
        try (InputStream in = resource.openStream()) {
            ByteStreams.copy(in, entityStream);
        }
    }
}
