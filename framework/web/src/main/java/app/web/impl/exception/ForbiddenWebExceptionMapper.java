package app.web.impl.exception;

import javax.annotation.Priority;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Response;

@Priority(Priorities.USER - 1000)
public class ForbiddenWebExceptionMapper extends AbstractWebApplicationExceptionMapper<ForbiddenException> {
    public ForbiddenWebExceptionMapper() {
        super(Response.Status.FORBIDDEN);
    }
}