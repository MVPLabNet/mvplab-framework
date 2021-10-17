package app.web.impl.exception;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@Priority(Priorities.USER - 1000)
public class DefaultWebExceptionMapper extends AbstractWebApplicationExceptionMapper<WebApplicationException> {
    public DefaultWebExceptionMapper() {
        super(Response.Status.INTERNAL_SERVER_ERROR);
    }
}
