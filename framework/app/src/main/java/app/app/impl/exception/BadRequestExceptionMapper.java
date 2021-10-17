package app.app.impl.exception;

import jakarta.annotation.Priority;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

@Priority(Priorities.USER)
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {
    @Override
    public Response toResponse(BadRequestException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.errorMessage = e.getMessage();
        return Response.status(Response.Status.METHOD_NOT_ALLOWED).entity(response)
            .type(MediaType.APPLICATION_JSON).build();
    }
}
