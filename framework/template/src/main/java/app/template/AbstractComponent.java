package app.template;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chi
 */
public abstract class AbstractComponent implements Component {
    private final String name;
    private final Map<String, ComponentAttribute<?>> attributes;

    public AbstractComponent(String name, List<ComponentAttribute<?>> attributes) {
        this.name = name;
        this.attributes = attributes.stream().collect(Collectors.toMap(ComponentAttribute::name, attribute -> attribute));
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Map<String, ComponentAttribute<?>> attributes() {
        return attributes;
    }
}
