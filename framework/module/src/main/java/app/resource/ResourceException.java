package app.resource;

import org.slf4j.helpers.MessageFormatter;

/**
 * @author chi
 */
public class ResourceException extends RuntimeException {
    public ResourceException(String message, Object... args) {
        super(MessageFormatter.arrayFormat(message, args).getMessage());
    }

    public ResourceException(Throwable e) {
        super(e);
    }
}
