package app.app.impl.exception;

import javax.annotation.Priority;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

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