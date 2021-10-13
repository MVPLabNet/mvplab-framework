package app.template.impl.processor;

import app.template.Attribute;
import app.template.Element;
import app.template.ElementProcessor;
import com.google.common.base.Splitter;
import app.resource.Resource;

import java.util.List;
import java.util.Optional;

/**
 * @author chi
 */
public class ForElementProcessorImpl implements ElementProcessor {
    private static final String FOR = "for";

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public Element process(Element element, Resource resource) {
        Optional<Attribute> attributeOptional = element.attribute(FOR);
        if (attributeOptional.isPresent() && attributeOptional.get().isDynamic()) {
            Attribute attribute = attributeOptional.get();
            Element forElement = new Element(FOR, true, attribute.row(), attribute.column(), attribute.source());
            element.parent().replaceChild(element, forElement);

            List<String> parts = Splitter.on(":").trimResults().splitToList(attribute.value());
            if (parts.size() != 2) {
                throw new RuntimeException(String.format("invalid for expression, expression=%s", attribute.value()));
            }

            Attribute item = new Attribute("item", false, attribute.row(), attribute.column(), attribute.source());
            item.setValue(parts.get(0));
            forElement.addAttribute(item);

            Attribute items = new Attribute("items", true, attribute.row(), attribute.column(), attribute.source());
            items.setValue(parts.get(1));
            forElement.addAttribute(items);

            forElement.setParent(element.parent());
            forElement.addChild(element);
            element.deleteAttribute(FOR);
            return forElement;
        }
        return element;
    }
}
