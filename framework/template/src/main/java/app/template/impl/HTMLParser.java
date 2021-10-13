package app.template.impl;

import app.resource.Resource;
import app.template.Attribute;
import app.template.Comment;
import app.template.DocType;
import app.template.Element;
import app.template.Node;
import app.template.Text;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.htmlparser.jericho.RowColumnVector;
import net.htmlparser.jericho.Source;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author chi
 */
public class HTMLParser {
    private final Resource resource;
    private static final String PREFIX = "j:";

    public HTMLParser(Resource resource) {
        this.resource = resource;
    }

    public List<Node> parse() throws IOException {
        List<Node> nodes = new ArrayList<>();
        String template = resource.toText(Charsets.UTF_8);
        Source doc = new Source(new StringReader(template));

        List<net.htmlparser.jericho.Element> childElements = doc.getChildElements();
        if (childElements.isEmpty()) {
            nodes.add(textNode(template, 0, 0, resource.path()));
        } else {
            for (net.htmlparser.jericho.Element element : childElements) {
                nodes.add(parse(element, null));
            }
        }
        return nodes;
    }

    private Element parse(net.htmlparser.jericho.Element htmlElement, Element parent) {
        RowColumnVector position = htmlElement.getRowColumnVector();

        if (isComment(htmlElement)) {
            Comment comment = new Comment(htmlElement.toString(), position.getRow(), position.getColumn(), resource.path());
            comment.setParent(parent);
            return comment;
        }

        if (isDocType(htmlElement)) {
            DocType docType = new DocType(htmlElement.toString(), position.getRow(), position.getColumn(), resource.path());
            docType.setParent(parent);
            return docType;
        }

        boolean dynamic = isDynamicElement(htmlElement.getName());
        Element element = new Element(name(htmlElement), dynamic, position.getRow(), position.getColumn(), resource.path());
        element.setParent(parent);
        parseAttributes(htmlElement).forEach(element::addAttribute);

        int last = htmlElement.getContent().getBegin();
        for (net.htmlparser.jericho.Element child : htmlElement.getChildElements()) {
            int current = child.getBegin();

            if (current > last) {
                String content = htmlElement.getSource().subSequence(last, current).toString().trim();
                if (content.length() != 0) {
                    Text text = textNode(content, position.getRow(), position.getColumn(), resource.path());
                    text.setParent(element);
                    element.addChild(text);
                }
            }

            element.addChild(parse(child, element));
            last = child.getEnd();
        }

        if (last < htmlElement.getContent().getEnd()) {
            String content = htmlElement.getSource().subSequence(last, htmlElement.getContent().getEnd()).toString().trim();
            if (content.length() != 0) {
                Text text = textNode(content, position.getRow(), position.getColumn(), resource.path());
                text.setParent(element);
                element.addChild(text);
            }
        }
        return element;
    }

    private String name(net.htmlparser.jericho.Element htmlElement) {
        return elementName(htmlElement.getName());
    }

    private boolean isComment(net.htmlparser.jericho.Element element) {
        return element.getName().startsWith("!--");
    }

    private boolean isDocType(net.htmlparser.jericho.Element element) {
        return element.getName().startsWith("!doctype");
    }

    private List<Attribute> parseAttributes(net.htmlparser.jericho.Element htmlElement) {
        if (htmlElement.getAttributes() == null) {
            return ImmutableList.of();
        }

        Map<String, Attribute> attributes = Maps.newLinkedHashMap();
        htmlElement.getAttributes().forEach(htmlAttribute -> {
            String attributeName = htmlAttribute.getName();
            boolean dynamic = isDynamicAttribute(attributeName);

            RowColumnVector position = htmlAttribute.getRowColumnVector();
            Attribute attribute = new Attribute(attributeName(attributeName), dynamic, position.getRow(), position.getColumn(), resource.path());
            String attributeValue = htmlAttribute.getValue();
            attribute.setValue(attributeValue);
            Attribute exist = attributes.get(attribute.name());
            if (exist == null) {
                attributes.put(attribute.name(), attribute);
            } else if (!exist.isDynamic() && attribute.isDynamic()) {
                attributes.put(attribute.name(), attribute);
            }
        });

        return Lists.newArrayList(attributes.values());
    }

    private boolean isDynamicElement(String elementName) {
        return elementName.startsWith(PREFIX);
    }

    private String elementName(String elementName) {
        return elementName.startsWith(PREFIX) ? elementName.substring(PREFIX.length()).toLowerCase(Locale.ENGLISH) : elementName;
    }

    private boolean isDynamicAttribute(String name) {
        return name.startsWith(PREFIX);
    }

    private String attributeName(String name) {
        return name.startsWith(PREFIX) ? name.substring(PREFIX.length()) : name;
    }

    private Text textNode(String content, Integer row, Integer column, String source) {
        return new Text(content, row, column, source);
    }
}
