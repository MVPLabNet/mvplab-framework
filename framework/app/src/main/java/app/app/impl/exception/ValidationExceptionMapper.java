package app.app.impl.exception;


import jakarta.annotation.Priority;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

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
