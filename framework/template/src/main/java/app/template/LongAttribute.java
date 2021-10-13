package app.template;

import java.util.Map;

public class LongAttribute extends ComponentAttribute<Long> {
    public LongAttribute(String name, Long defaultValue) {
        super(name, Long.class, defaultValue);
    }

    @Override
    public Long value(Map<String, Object> attributes) {
        Long value = (Long) attributes.get(name());
        if (value == null) {
            value = defaultValue();
        }
        return value;
    }
}