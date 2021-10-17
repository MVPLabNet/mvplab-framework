package app.web.impl.exception;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;

@Priority(Priorities.USER - 1000)
public class ForbiddenWebExceptionMapper extends AbstractWebApplicationExceptionMapper<ForbiddenException> {
    public ForbiddenWebExceptionMapper() {
        super(Response.Status.FORBIDDEN);
    }
}
