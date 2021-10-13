package app.app.impl.exception;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

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