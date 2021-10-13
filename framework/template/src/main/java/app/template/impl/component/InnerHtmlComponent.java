package app.template.impl.component;

import app.template.AbstractComponent;
import app.template.Attributes;
import app.template.Children;
import app.template.ObjectAttribute;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author chi
 */
public class InnerHtmlComponent extends AbstractComponent {
    public InnerHtmlComponent() {
        super("innerHtml", Lists.newArrayList(new ObjectAttribute<>("content", Object.class, null)));
    }

    @Override
    public void output(Map<String, Object> bindings, Attributes attributes, Children children, OutputStream out) throws IOException {
        Object content = attributes.get("content");
        if (content == null) {
            children.output(bindings, out);
        } else {
            out.write(content.toString().getBytes(Charsets.UTF_8));
        }
    }
}
