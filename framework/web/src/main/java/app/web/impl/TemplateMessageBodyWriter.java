package app.web.impl;

import app.template.TemplateEngine;
import app.web.TemplateEntity;
import jakarta.annotation.Priority;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author chi
 */
@Priority(Priorities.ENTITY_CODER - 100)
public class TemplateMessageBodyWriter implements MessageBodyWriter<Object> {
    private final Logger logger = LoggerFactory.getLogger(TemplateMessageBodyWriter.class);

    @Inject
    TemplateEngine templateEngine;

    @Override
    public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return TemplateEntity.class.isAssignableFrom(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeTo(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        TemplateEntity view = (TemplateEntity) o;
        app.template.Template template = templateEngine.template(view.path).orElseThrow(() -> new NotFoundException(String.format("missing template, path=%s", view.path)));
        template.output(view.bindings, entityStream);

        if (!httpHeaders.containsKey(HttpHeaders.CONTENT_TYPE)) {
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML);
        }
    }
}
