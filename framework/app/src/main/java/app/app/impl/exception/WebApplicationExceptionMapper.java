package app.app.impl.exception;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

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
