package app.web.impl.exception;

import jakarta.annotation.Priority;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.process.internal.RequestScoped;

@RequestScoped
@Priority(Priorities.USER - 1000)
public class BadRequestWebExceptionMapper extends AbstractWebApplicationExceptionMapper<BadRequestException> {
    public BadRequestWebExceptionMapper() {
        super(Response.Status.BAD_REQUEST);
    }
}
