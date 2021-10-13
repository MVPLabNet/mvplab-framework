package app.template.impl.processor;

import app.template.Attribute;
import app.template.Element;
import app.template.ElementProcessor;
import app.resource.Resource;

import java.util.Optional;

/**
 * @author chi
 */
public class IfElementProcessorImpl implements ElementProcessor {
    private static final String IF = "if";

    @Override
    public int priority() {
        return 10;
    }

    @Override
    public Element process(Element element, Resource resource) {
        Optional<Attribute> attributeOptional = element.attribute(IF);
        if (attributeOptional.isPresent() && attributeOptional.get().isDynamic()) {
            Attribute attribute = attributeOptional.get();
            Element ifElement = new Element(IF, true, attribute.row(), attribute.column(), attribute.source());
            element.parent().replaceChild(element, ifElement);
            Attribute condition = new Attribute("condition", true, attribute.row(), attribute.column(), attribute.source());
            condition.setValue(attribute.value());
            ifElement.addAttribute(condition);

            ifElement.setParent(element.parent());
            ifElement.addChild(element);

            element.deleteAttribute(IF);
            return ifElement;
        }
        return element;
    }
}
