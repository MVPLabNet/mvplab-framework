package app.web.impl.exception;

import app.template.Template;
import app.template.TemplateEngine;
import app.util.exception.Errors;
import app.web.RequestInfo;
import app.web.WebOptions;
import com.google.common.collect.Maps;
import org.glassfish.jersey.process.internal.RequestScoped;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Optional;

/**
 * @author chi
 */
@RequestScoped
public abstract class AbstractWebApplicationExceptionMapper<T extends WebApplicationException> implements ExceptionMapper<T> {
    private final Response.Status statusCode;
    @Inject
    WebOptions webOptions;
    @Inject
    TemplateEngine templateEngine;
    @Inject
    Provider<RequestInfo> provider;

    protected AbstractWebApplicationExceptionMapper(Response.Status statusCode) {
        this.statusCode = statusCode;
    }

    protected boolean isAcceptJSON() {
        RequestInfo requestInfo = provider.get();
        String accept = requestInfo.headers().get("Accept");
        return MediaType.APPLICATION_JSON.equalsIgnoreCase(accept);
    }

    @Override
    public Response toResponse(T e) {
        if (isAcceptJSON()) {
            ExceptionResponse response = new ExceptionResponse();
            response.errorMessage = e.getMessage();
            return Response.status(statusCode).entity(response)
                .type(MediaType.APPLICATION_JSON).build();
        } else {
            Optional<Template> template = templateEngine.template(templatePath());
            if (template.isPresent()) {
                Map<String, Object> bindings = Maps.newHashMap();
                bindings.put("e", e);
                bindings.put("page", null);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    template.get().output(bindings, out);
                    return Response.ok(out.toByteArray()).status(statusCode).type(MediaType.TEXT_HTML).build();
                } catch (Throwable ex) {
                    ex.addSuppressed(e);
                    return printStackTrace(ex);
                }
            }
            return printStackTrace(e);
        }
    }

    protected Response printStackTrace(Throwable e) {
        return Response.ok(Errors.stackTrace(e)).status(statusCode).type(MediaType.TEXT_PLAIN).build();
    }

    protected String templatePath() {
        String templatePath = webOptions.errorPages.get(statusCode.getStatusCode());
        if (templatePath == null) {
            templatePath = webOptions.errorPages.get(500);
        }
        return templatePath;
    }
}
