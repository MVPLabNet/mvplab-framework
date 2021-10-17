package app;

import jakarta.ws.rs.WebApplicationException;
import org.slf4j.helpers.MessageFormatter;


/**
 * @author chi
 */
public class ApplicationException extends WebApplicationException {
    public ApplicationException(Throwable e) {
        super(e);
    }

    public ApplicationException(String message, Object... args) {
        super(MessageFormatter.arrayFormat(message, args).getMessage());

        if (args.length > 0 && args[args.length - 1] instanceof Throwable) {
            addSuppressed((Throwable) args[args.length - 1]);
        }
    }
}
