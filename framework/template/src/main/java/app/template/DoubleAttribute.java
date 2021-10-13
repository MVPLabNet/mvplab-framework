package app.template;

import java.util.Map;

public class DoubleAttribute extends ComponentAttribute<Double> {
    public DoubleAttribute(String name, Double defaultValue) {
        super(name, Double.class, defaultValue);
    }

    @Override
    public Double value(Map<String, Object> attributes) {
        if (attributes.containsKey(name())) {
            return (Double) attributes.get(name());
        }
        return defaultValue();
    }
}