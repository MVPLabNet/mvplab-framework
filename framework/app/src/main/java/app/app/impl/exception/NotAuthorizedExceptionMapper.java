package app.app.impl.exception;

import jakarta.annotation.Priority;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

@Priority(Priorities.USER)
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {
    @Override
    public Response toResponse(NotAuthorizedException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.errorMessage = e.getMessage();
        return Response.status(Response.Status.UNAUTHORIZED).entity(response)
            .type(MediaType.APPLICATION_JSON).build();
    }
}
