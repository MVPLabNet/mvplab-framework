package app.template.impl.processor;

import app.resource.Resource;
import app.template.Attribute;
import app.template.Element;
import app.template.ElementProcessor;
import com.google.common.collect.Lists;

import java.util.Optional;

/**
 * @author chi
 */
public class InnerHtmlElementProcessorImpl implements ElementProcessor {
    private static final String INNER_HTML = "innerHtml";

    @Override
    public int priority() {
        return 50;
    }

    @Override
    public Element process(Element element, Resource resource) {
        Optional<Attribute> attributeOptional = element.attribute(INNER_HTML);
        if (!element.name().equalsIgnoreCase(INNER_HTML) && attributeOptional.isPresent() && attributeOptional.get().isDynamic()) {
            Attribute attribute = attributeOptional.get();
            Element htmlElement = new Element(INNER_HTML, true, attribute.row(), attribute.column(), attribute.source());
            htmlElement.setParent(element);
            Attribute content = new Attribute("content", true, attribute.row(), attribute.column(), attribute.source());
            content.setValue(attribute.value());
            htmlElement.addAttribute(content);
            htmlElement.setChildren(element.children());

            element.deleteAttribute(INNER_HTML);
            element.setChildren(Lists.newArrayList(htmlElement));
        }
        return element;
    }
}
