package app.template;

import app.ApplicationException;

/**
 * @author chi
 */
public class TemplateResourceException extends ApplicationException {
    public TemplateResourceException(String message, Object... args) {
        super(message, args);
    }
}
