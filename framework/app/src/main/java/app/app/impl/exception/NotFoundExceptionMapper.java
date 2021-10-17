package app.app.impl.exception;

import jakarta.annotation.Priority;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

@Priority(Priorities.USER)
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException exception) {
        ExceptionResponse response = new ExceptionResponse();
        response.errorMessage = exception.getMessage();
        return Response.status(404).entity(response)
            .type(MediaType.APPLICATION_JSON).build();
    }
}
