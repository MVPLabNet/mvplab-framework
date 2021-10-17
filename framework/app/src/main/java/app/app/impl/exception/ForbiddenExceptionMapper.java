package app.app.impl.exception;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

@Priority(Priorities.USER)
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {
    @Override
    public Response toResponse(ForbiddenException exception) {
        ExceptionResponse response = new ExceptionResponse();
        response.errorMessage = exception.getMessage();
        return Response.status(Response.Status.FORBIDDEN).entity(response)
            .type(MediaType.APPLICATION_JSON).build();
    }
}
