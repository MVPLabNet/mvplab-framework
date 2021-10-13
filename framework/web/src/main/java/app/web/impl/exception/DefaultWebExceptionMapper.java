package app.web.impl.exception;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Priority(Priorities.USER - 1000)
public class DefaultWebExceptionMapper extends AbstractWebApplicationExceptionMapper<WebApplicationException> {
    public DefaultWebExceptionMapper() {
        super(Response.Status.INTERNAL_SERVER_ERROR);
    }
}