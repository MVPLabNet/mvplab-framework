package app.template;

import java.util.Map;

public class IntegerAttribute extends ComponentAttribute<Integer> {
    public IntegerAttribute(String name, Integer defaultValue) {
        super(name, Integer.class, defaultValue);
    }

    @Override
    public Integer value(Map<String, Object> attributes) {
        Integer value = (Integer) attributes.get(name());
        if (value == null) {
            value = defaultValue();
        }
        return value;
    }
}