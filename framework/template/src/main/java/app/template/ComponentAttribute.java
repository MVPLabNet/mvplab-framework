package app.template;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author chi
 */
public abstract class ComponentAttribute<T> {
    private final String name;
    private final Type type;
    private final T defaultValue;

    protected ComponentAttribute(String name, Type type, T defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public String name() {
        return name;
    }

    public Type type() {
        return type;
    }

    public T defaultValue() {
        return defaultValue;
    }

    public abstract T value(Map<String, Object> attributes);
}
