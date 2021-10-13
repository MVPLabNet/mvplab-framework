package app.template;

import java.util.Map;

public class BooleanAttribute extends ComponentAttribute<Boolean> {
    public BooleanAttribute(String name, Boolean defaultValue) {
        super(name, Boolean.class, defaultValue);
    }

    @Override
    public Boolean value(Map<String, Object> attributes) {
        Boolean value = (Boolean) attributes.get(name());
        if (value == null) {
            value = defaultValue();
        }
        return value;
    }
}