package app.template.impl.component;

import app.template.AbstractComponent;
import app.template.Attributes;
import app.template.Children;
import app.template.ObjectAttribute;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author chi
 */
public class IfComponent extends AbstractComponent {
    public IfComponent() {
        super("if", Lists.newArrayList(new ObjectAttribute<>("condition", Boolean.class, false)));
    }

    @Override
    public void output(Map<String, Object> bindings, Attributes attributes, Children children, OutputStream out) throws IOException {
        Boolean condition = attributes.get("condition");

        if (Boolean.TRUE.equals(condition)) {
            children.output(bindings, out);
        }
    }
}
