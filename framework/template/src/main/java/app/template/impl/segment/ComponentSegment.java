package app.template.impl.segment;

import app.template.Attributes;
import app.template.Children;
import app.template.Component;
import app.template.Expression;
import app.template.impl.Segment;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author chi
 */
public class ComponentSegment implements Segment {
    private final Component component;
    private final Children children;
    private final Map<String, Expression> attributes;

    public ComponentSegment(Component component, Children children, Map<String, Expression> attributes) {
        this.component = component;
        this.children = children;
        this.attributes = attributes;
    }

    @Override
    public void output(Map<String, Object> bindings, OutputStream outputStream) throws IOException {
        Map<String, Object> attributeValues = Maps.newHashMapWithExpectedSize(this.attributes.size());
        this.attributes.forEach((key, expression) -> attributeValues.put(key, expression.eval(bindings)));
        component.output(bindings, new Attributes(attributeValues), children, outputStream);
    }
}
