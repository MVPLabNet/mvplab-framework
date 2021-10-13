package app.web.impl.exception;

import org.glassfish.jersey.process.internal.RequestScoped;

import javax.annotation.Priority;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Response;

@RequestScoped
@Priority(Priorities.USER - 1000)
public class BadRequestWebExceptionMapper extends AbstractWebApplicationExceptionMapper<BadRequestException> {
    public BadRequestWebExceptionMapper() {
        super(Response.Status.BAD_REQUEST);
    }
}