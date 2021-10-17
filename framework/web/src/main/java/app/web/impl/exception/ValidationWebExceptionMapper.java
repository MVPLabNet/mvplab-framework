package app.web.impl.exception;

import jakarta.annotation.Priority;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.glassfish.jersey.process.internal.RequestScoped;

@RequestScoped
@Priority(Priorities.USER - 1000)
public class ValidationWebExceptionMapper implements ExceptionMapper<ValidationException> {
    @Override
    public Response toResponse(ValidationException exception) {
        ValidationWebExceptionResponse response = new ValidationWebExceptionResponse();
        if (exception instanceof ConstraintViolationException) {
            ConstraintViolation<?> violation = ((ConstraintViolationException) exception).getConstraintViolations().iterator().next();
            response.field = violation.getPropertyPath().toString();
            response.errorMessage = violation.getMessage();
        } else {
            response.errorMessage = exception.getMessage();
        }
        return Response.status(400).entity(response)
            .type(MediaType.APPLICATION_JSON).build();
    }
}
