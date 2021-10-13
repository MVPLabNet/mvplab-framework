package app.template.impl.component;

import app.template.AbstractComponent;
import app.template.Attributes;
import app.template.Children;
import app.template.ObjectAttribute;
import app.template.StringAttribute;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author chi
 */
public class ForComponent extends AbstractComponent {
    public ForComponent() {
        super("for", Lists.newArrayList(new StringAttribute("item", "item"), new StringAttribute("index", "$index"), new ObjectAttribute<>("items", Iterable.class, null)));
    }

    @Override
    public void output(Map<String, Object> bindings, Attributes attributes, Children children, OutputStream out) throws IOException {
        Iterable<?> items = attributes.get("items");
        String item = attributes.get("item");
        String index = attributes.get("index");

        if (items != null) {
            int i = 0;
            Map<String, Object> scopedBindings = Maps.newHashMap();
            scopedBindings.putAll(bindings);
            for (Object value : items) {
                scopedBindings.put(item, value);
                scopedBindings.put(index, i++);
                children.output(scopedBindings, out);
            }
        }
    }
}
