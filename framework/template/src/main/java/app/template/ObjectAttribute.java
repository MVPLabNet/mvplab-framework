package app.template;

import app.util.JSON;
import com.google.common.base.Strings;

import java.lang.reflect.Type;
import java.util.Map;

public class ObjectAttribute<T> extends ComponentAttribute<T> {
    public ObjectAttribute(String name, Type type, T defaultValue) {
        super(name, type, defaultValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T value(Map<String, Object> attributes) {
        Object value = attributes.get(name());
        if (value == null) {
            value = defaultValue();
        }
        return convert(value);
    }

    private T convert(Object object) {
        if (isJSONValue(object)) {
            return JSON.fromJSON((String) object, type());
        }
        return (T) object;
    }

    private boolean isJSONValue(Object value) {
        if (!type().equals(String.class) && value instanceof String) {
            String text = ((String) value).trim();
            if (Strings.isNullOrEmpty(text) || text.length() < 2) {
                return false;
            }
            return text.charAt(0) == '{' && text.charAt(text.length() - 1) == '}';
        }
        return false;
    }
}