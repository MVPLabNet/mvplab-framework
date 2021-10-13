package app.app.impl.exception;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * @author chi
 */
@Priority(Priorities.USER)
public class WebApplicationExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public final Response toResponse(Throwable e) {
        ExceptionResponse response = new ExceptionResponse();
        response.errorMessage = e.getMessage();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response)
            .type(MediaType.APPLICATION_JSON).build();
    }
}
