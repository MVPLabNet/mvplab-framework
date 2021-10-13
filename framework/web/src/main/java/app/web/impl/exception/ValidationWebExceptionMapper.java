package app.web.impl.exception;

import org.glassfish.jersey.process.internal.RequestScoped;

import javax.annotation.Priority;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

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