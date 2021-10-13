package app.web.impl.exception;

import org.glassfish.jersey.process.internal.RequestScoped;

import javax.annotation.Priority;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Response;

@RequestScoped
@Priority(Priorities.USER - 1000)
public class NotFoundWebExceptionMapper extends AbstractWebApplicationExceptionMapper<NotFoundException> {
    public NotFoundWebExceptionMapper() {
        super(Response.Status.NOT_FOUND);
    }
}