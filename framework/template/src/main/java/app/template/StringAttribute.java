package app.template;

import java.util.Map;

public class StringAttribute extends ComponentAttribute<String> {
    public StringAttribute(String name, String defaultValue) {
        super(name, String.class, defaultValue);
    }

    @Override
    public String value(Map<String, Object> attributes) {
        String value = (String) attributes.get(name());
        if (value == null) {
            value = defaultValue();
        }
        return value;
    }
}