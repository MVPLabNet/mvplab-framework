package app.exception;

import app.util.exception.Errors;
import org.junit.jupiter.api.Test;

import javax.ws.rs.WebApplicationException;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * @author chi
 */
public class ErrorsTest {
    @Test
    public void dump() {
        String stackTrace = Errors.stackTrace(new WebApplicationException("it happens"));
        assertNotNull(stackTrace);
    }
}