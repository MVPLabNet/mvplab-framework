package app;

import org.slf4j.helpers.MessageFormatter;

import javax.ws.rs.WebApplicationException;

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
