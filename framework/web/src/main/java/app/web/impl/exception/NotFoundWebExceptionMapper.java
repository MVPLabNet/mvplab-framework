package app.web.impl.exception;

import jakarta.annotation.Priority;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.process.internal.RequestScoped;

@RequestScoped
@Priority(Priorities.USER - 1000)
public class NotFoundWebExceptionMapper extends AbstractWebApplicationExceptionMapper<NotFoundException> {
    public NotFoundWebExceptionMapper() {
        super(Response.Status.NOT_FOUND);
    }
}
