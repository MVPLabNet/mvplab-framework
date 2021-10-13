package app.app.impl.exception;

import javax.annotation.Priority;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

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