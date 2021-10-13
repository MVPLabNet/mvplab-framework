package app.app.impl.exception;

import javax.annotation.Priority;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

@Priority(Priorities.USER)
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {
    @Override
    public Response toResponse(ValidationException exception) {
        ValidationExceptionResponse response = new ValidationExceptionResponse();
        if (exception instanceof ConstraintViolationException) {
            ConstraintViolation<?> violation = ((ConstraintViolationException) exception).getConstraintViolations().iterator().next();
            response.path = violation.getPropertyPath().toString();
            response.errorMessage = violation.getMessage();
        } else {
            response.errorMessage = exception.getMessage();
        }
        return Response.status(400).entity(response)
            .type(MediaType.APPLICATION_JSON).build();
    }
}