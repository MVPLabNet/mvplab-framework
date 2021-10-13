package app.template;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author chi
 */
public class Element implements Node {
    private static final Set<String> SELF_CLOSED_ELEMENTS = new HashSet<>(Arrays.asList("area", "base", "br", "col", "embed", "hr", "img", "input",
        "keygen", "link", "meta", "param", "source", "track", "wbr"));
    private static final Set<String> BOOL_ATTRIBUTES = new HashSet<>(Arrays.asList("checked", "selected", "disabled", "readonly", "multiple",
        "ismap", "defer", "declare", "noresize", "nowrap", "noshade", "compact"));

    private final String name;
    private final boolean dynamic;
    private final Integer row;
    private final Integer column;
    private final String source;

    private Element parent;
    private final Map<String, Attribute> attributes = Maps.newHashMap();
    private List<Node> children = Lists.newArrayList();

    public Element(String name, boolean dynamic, Integer row, Integer column, String source) {
        this.name = name;
        this.dynamic = dynamic;
        this.row = row;
        this.column = column;
        this.source = source;
    }

    @Override
    public Element parent() {
        return parent;
    }

    @Override
    public void setParent(Element parent) {
        this.parent = parent;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public List<Attribute> attributes() {
        return ImmutableList.copyOf(attributes.values());
    }


    public Optional<Attribute> attribute(String name) {
        return Optional.ofNullable(attributes.get(name));
    }


    public void addAttribute(Attribute attribute) {
        attributes.put(attribute.name(), attribute);
    }


    public void deleteAttribute(String name) {
        attributes.remove(name);
    }

    public List<Node> children() {
        return children;
    }

    public void addChild(Node node) {
        node.setParent(this);
        children.add(node);
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public void replaceChild(Node node, Node withNode) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).equals(node)) {
                children.set(i, withNode);
                return;
            }
        }
    }

    public void deleteChild(Node node) {
        children.remove(node);
    }

    public String innerText() {
        StringBuilder b = new StringBuilder();
        for (Node child : Lists.newArrayList(children)) {
            if (child instanceof Element) {
                b.append(((Element) child).innerText());
            } else if (child.isText()) {
                b.append(((Text) child).innerText());
            }
        }
        return b.toString();
    }

    public String innerHtml() {
        if (children == null || children.isEmpty()) {
            return "";
        }
        StringBuilder b = new StringBuilder();
        for (Node child : children) {
            if (child.isText()) {
                b.append(((Text) child).innerText());
            } else if (child.isComment()) {
                b.append(((Comment) child).outerHtml());
            } else {
                Element element = (Element) child;
                b.append('<');
                b.append(element.name());
                element.attributes().forEach(attribute -> {
                    b.append(' ').append(attribute.name());
                    if (!isBoolAttribute(attribute)) {
                        b.append("=\"").append(attribute.value()).append('\"');
                    }
                });
                if (isSelfClosed(element)) {
                    b.append("/>");
                } else {
                    b.append('>');
                    b.append(element.innerHtml());
                    b.append("</").append(element.name()).append('>');
                }
            }

        }
        return b.toString();
    }

    private boolean isSelfClosed(Element element) {
        return SELF_CLOSED_ELEMENTS.contains(element.name());
    }

    private boolean isBoolAttribute(Attribute attribute) {
        return BOOL_ATTRIBUTES.contains(attribute.name());
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Integer row() {
        return row;
    }

    @Override
    public Integer column() {
        return column;
    }

    @Override
    public String source() {
        return source;
    }
}
