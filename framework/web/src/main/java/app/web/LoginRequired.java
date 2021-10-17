package app.web;

import jakarta.ws.rs.NameBinding;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author chi
 */
@NameBinding
@Retention(RUNTIME)
public @interface LoginRequired {
}
