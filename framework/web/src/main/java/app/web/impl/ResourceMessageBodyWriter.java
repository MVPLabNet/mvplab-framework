package app.web.impl;

import app.resource.Resource;
import com.google.common.io.ByteStreams;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
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
