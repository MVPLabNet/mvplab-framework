package app.template;

import java.util.Map;

/**
 * @author chi
 */
public interface Expression {
    Object eval(Map<String, Object> bindings);
}
