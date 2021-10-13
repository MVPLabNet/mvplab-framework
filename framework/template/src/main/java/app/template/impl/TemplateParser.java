package app.template.impl;

import app.resource.Resource;
import app.resource.ResourceRepository;
import app.template.Attribute;
import app.template.Comment;
import app.template.Component;
import app.template.ComponentAttribute;
import app.template.DocType;
import app.template.Element;
import app.template.ElementProcessor;
import app.template.Expression;
import app.template.Node;
import app.template.Script;
import app.template.StyleSheet;
import app.template.TemplateException;
import app.template.TemplateResourceException;
import app.template.Text;
import app.template.impl.segment.AttributeExpressionSegment;
import app.template.impl.segment.ComponentSegment;
import app.template.impl.segment.ElementSegment;
import app.template.impl.segment.ExpressionSegment;
import app.template.impl.segment.IfSegment;
import app.template.impl.segment.StaticSegment;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.jexl3.JexlEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chi
 */
public class TemplateParser {
    private static final Set<String> SELF_CLOSED_ELEMENTS = Sets.newHashSet("area", "base", "br", "col", "embed", "hr", "img", "input",
        "keygen", "link", "meta", "param", "source", "track", "wbr");
    static final Set<String> BOOL_ATTRIBUTES = Sets.newHashSet("defer", "formNoValidate", "nowrap", "contenteditable", "controls", "compact",
        "hidden", "declare", "indeterminate", "required", "translate", "ismap", "noshade", "default", "scrolling", "itemscope", "novalidate",
        "readonly", "loop", "noresize", "checked", "disabled", "frameborder", "muted", "selected", "border", "seamless", "autocomplete",
        "multiple", "sortable", "autofocus", "autoplay", "nohref", "async", "scoped", "spellcheck", "challenge", "open", "reversed");

    private final Resource resource;
    private final boolean asComponent;
    private final boolean parseComment;
    private final ElementProcessorRegistry elementProcessorRegistry;
    private final ComponentRegistry componentRegistry;
    private final ResourceRepository resourceRepository;
    private final List<Script> scripts = new ArrayList<>();
    private final List<StyleSheet> styles = new ArrayList<>();
    private final List<Segment> segments = new ArrayList<>();
    private final List<Component> componentRefs = Lists.newArrayList();
    private final TemplateModel templateModel;

    public TemplateParser(Resource resource, boolean asComponent, boolean parseComment, ResourceRepository resourceRepository, ComponentRegistry componentRegistry,
                          ElementProcessorRegistry elementProcessorRegistry, JexlEngine jexlEngine) {
        this.resource = resource;
        this.asComponent = asComponent;
        this.parseComment = parseComment;
        this.resourceRepository = resourceRepository;
        this.elementProcessorRegistry = elementProcessorRegistry;
        this.componentRegistry = componentRegistry;


        templateModel = new JEXLTemplateModel(jexlEngine);
    }

    @SuppressWarnings("checkstyle:NestedIfDepth")
    public TemplateImpl parse() {
        try {
            List<Node> elements = new HTMLParser(resource).parse();
            elements.forEach(this::process);

            Optional<Element> htmlOptional = html(elements);
            if (htmlOptional.isPresent()) {
                Element html = htmlOptional.get();
                head(html).ifPresent(element -> {
                    for (Node child : element.children()) {
                        parseStyle(child);
                        parseScript(child);
                    }
                });
                parseComponents();

                if (asComponent) {
                    Optional<Element> body = body(html);
                    if (!body.isPresent()) {
                        throw new TemplateResourceException("missing tag <body>, path={}", resource.path());
                    }
                    body.get().children().forEach(element -> segments.add(segment(element)));
                } else {
                    head(html).ifPresent(element -> appendStyleElement(element, inlineStyles(componentRefs)));
                    body(html).ifPresent(element -> appendScriptElement(element, inlineScripts(componentRefs)));

                    segments.addAll(elements.stream().map(this::segment).collect(Collectors.toList()));
                }
            } else {
                if (asComponent) {
                    throw new TemplateResourceException("missing tag <html>, path={}", resource.path());
                } else {
                    segments.addAll(elements.stream().map(this::segment).collect(Collectors.toList()));
                }
            }
            return new TemplateImpl(resource.path(), segments, componentRefs, scripts, styles);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void process(Node node) {
        if (node instanceof Element) {
            Element element = (Element) node;
            for (ElementProcessor processor : elementProcessorRegistry.processors()) {
                processor.process(element, resource);
            }

            if (element.isDynamic()) {
                Component component = component(element);
                componentRefs.add(component);
            }

            for (int i = 0; i < element.children().size(); i++) {
                process(element.children().get(i));
            }
        }
    }

    private Component component(Element element) {
        Optional<Component> component = componentRegistry.component(element.name());
        return component.orElseThrow(() -> new TemplateException("missing component, name={}, template={}", element.name(), resource.path()));
    }

    private String inlineStyles(List<Component> components) {
        Set<String> visited = Sets.newHashSet(styles.stream().filter(input -> input.href != null).map(styleSheet -> styleSheet.href).collect(Collectors.toList()));
        StringBuilder b = new StringBuilder();
        for (Component component : components) {
            for (StyleSheet style : component.styles()) {
                if (!Strings.isNullOrEmpty(style.innerText)) {
                    b.append(style.innerText);
                    b.append('\n');
                } else {
                    if (!visited.contains(style.href)) {
                        Optional<Resource> resource = resourceRepository.get(style.href.substring(1));
                        resource.ifPresent(r -> b.append(r.toText(Charsets.UTF_8)));
                        b.append('\n');
                        visited.add(style.href);
                    }
                }
            }
        }
        return b.toString();
    }

    private String inlineScripts(List<Component> components) {
        Set<String> visited = Sets.newHashSet(scripts.stream().filter(input -> input.src != null).map(script -> script.src).collect(Collectors.toList()));
        StringBuilder b = new StringBuilder();
        for (Component component : components) {
            for (Script script : component.scripts()) {
                if (!Strings.isNullOrEmpty(script.innerText)) {
                    b.append(script.innerText);
                    b.append("\n;");
                } else {
                    if (!visited.contains(script.src)) {
                        Optional<Resource> resource = resourceRepository.get(script.src.substring(1));
                        resource.ifPresent(resource1 -> b.append(resource1.toText(Charsets.UTF_8)));
                        b.append("\n;");
                        visited.add(script.src);
                    }
                }
            }
        }
        return b.toString();
    }

    private void appendScriptElement(Element element, String innerText) {
        if (!Strings.isNullOrEmpty(innerText)) {
            Element scriptElement = new Element("script", false, element.row(), element.column(), element.source());
            Text text = new Text(innerText, element.row(), element.column(), element.source());
            scriptElement.addChild(text);
            element.addChild(scriptElement);
        }
    }

    private void appendStyleElement(Element element, String innerText) {
        if (!Strings.isNullOrEmpty(innerText)) {
            Element styleElement = new Element("style", false, element.row(), element.column(), element.source());

            Text text = new Text(innerText, element.row(), element.column(), element.source());
            styleElement.addChild(text);
            element.addChild(styleElement);
        }
    }

    private void parseScript(Node node) {
        String elementName = node.name();
        if ("script".equals(elementName)) {
            Element element = (Element) node;
            Optional<Attribute> type = element.attribute("type");
            if (type.isPresent() && !"text/javascript".equals(type.get().value())) {
                return;
            }
            Optional<Attribute> src = element.attribute("src");
            Script script = new Script();
            if (src.isPresent()) {
                script.src = src.get().value();
            } else {
                script.innerText = element.innerText();
            }
            scripts.add(script);
        }
    }

    private void parseStyle(Node node) {
        String elementName = node.name();
        if ("link".equals(elementName)) {
            Element element = (Element) node;
            Optional<Attribute> ref = element.attribute("rel");
            if (ref.isEmpty() || !"stylesheet".equalsIgnoreCase(ref.get().value())) {
                return;
            }
            Optional<Attribute> href = element.attribute("href");
            StyleSheet style = new StyleSheet();
            if (href.isPresent()) {
                style.href = href.get().value();
                styles.add(style);
            }
        } else if ("style".equals(elementName)) {
            Element element = (Element) node;
            Optional<Attribute> type = element.attribute("type");
            if (type.isEmpty() || !"stylesheet".equalsIgnoreCase(type.get().value())) {
                return;
            }
            StyleSheet style = new StyleSheet();
            style.innerText = element.innerText();
            styles.add(style);
        }
    }

    private void parseComponents() {
        Deque<Component> stack = Lists.newLinkedList(componentRefs);
        Set<String> visited = Sets.newHashSet();
        while (!stack.isEmpty()) {
            Component current = stack.pollFirst();
            if (current != null && !visited.contains(current.name())) {
                visited.add(current.name());
                stack.addAll(current.refs());
                componentRefs.add(current);
            }
        }
    }

    private Optional<Element> html(List<Node> nodes) {
        for (Node node : nodes) {
            if ("html".equalsIgnoreCase(node.name())) {
                return Optional.of((Element) node);
            }
        }
        return Optional.empty();
    }

    private Optional<Element> head(Element html) {
        for (Node child : html.children()) {
            if ("head".equalsIgnoreCase(child.name())) {
                return Optional.of((Element) child);
            }
        }
        return Optional.empty();
    }

    private Optional<Element> body(Element html) {
        for (Node child : html.children()) {
            if ("body".equalsIgnoreCase(child.name())) {
                return Optional.of((Element) child);
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("checkstyle:NestedIfDepth")
    private Segment segment(Node node) {
        if (node.isText()) {
            String text = ((Text) node).innerText();
            boolean resourceText = isResourceElement(node.parent());
            if (resourceText) {
                return new StaticSegment(text);
            }

            TemplateLiteral literal = new TemplateLiteral(text);
            if (literal.isDynamic()) {
                Expression textExpression = templateModel.add(literal.expr(), null, node.row(), node.column(), node.source());
                return new ExpressionSegment(textExpression);
            }
            return new StaticSegment(text);
        }

        if (node.isComment()) {
            Comment comment = (Comment) node;
            if (parseComment) {
                return new StaticSegment(comment.outerHtml());
            } else if (isMSComment(comment)) {
                return new StaticSegment(comment.outerHtml());
            } else {
                return new StaticSegment("");
            }
        }

        if (node.isDocType()) {
            return new StaticSegment(((DocType) node).outerHtml());
        }

        Element element = (Element) node;
        if (element.isDynamic()) {
            return dynamicSegment(element);
        } else {
            return staticSegment(element);
        }
    }

    private Segment staticSegment(Element element) {
        List<Segment> start = new ArrayList<>();
        List<Segment> end = new ArrayList<>();
        start.add(new StaticSegment("<" + element.name()));
        element.attributes().forEach(attribute -> {
            TemplateLiteral literal = new TemplateLiteral(attribute.value());
            if (literal.isDynamic()) {
                if (isBoolAttribute(attribute.name())) {
                    IfSegment attributeIfSegment = new IfSegment(parseExpression(element, literal.expr(), attribute.source()));
                    attributeIfSegment.addChild(new StaticSegment(' ' + attribute.name()));
                    start.add(attributeIfSegment);
                } else {
                    start.add(new StaticSegment(' ' + attribute.name() + "=\""));
                    start.add(new AttributeExpressionSegment(parseExpression(element, ' ' + literal.expr(), attribute.source())));
                    start.add(new StaticSegment("\""));
                }
            } else {
                if (isBoolAttribute(attribute.name())) {
                    start.add(new StaticSegment(' ' + attribute.name()));
                } else {
                    start.add(new StaticSegment(' ' + attribute.name() + "=\"" + attribute.value() + "\""));
                }
            }
        });

        if (isSelfClosedElement(element.name())) {
            start.add(new StaticSegment("/>"));
        } else {
            start.add(new StaticSegment(">"));
            end.add(new StaticSegment("</" + element.name() + ">"));
        }

        ElementSegment segment = new ElementSegment(start, end);
        for (Node child : element.children()) {
            segment.addChild(segment(child));
        }
        return segment;
    }

    private boolean isResourceElement(Element element) {
        if (element == null) {
            return false;
        }
        if ("script".equals(element.name())) {
            return true;
        }
        if ("link".equals(element.name())) {
            Optional<Attribute> attribute = element.attribute("rel");
            return attribute.isPresent() && attribute.get().value().equals("stylesheet");
        }
        return false;
    }

    private Segment dynamicSegment(Element element) {
        String componentName = element.name();
        Map<String, Expression> attributeExpressions = new HashMap<>();
        element.attributes().forEach(attribute -> {
            TemplateLiteral literal = new TemplateLiteral(attribute.value());
            if (literal.isDynamic()) {
                attributeExpressions.put(attribute.name(), parseExpression(element, literal.expr(), element.source()));
            } else {
                attributeExpressions.put(attribute.name(), new ConstantExpression(attribute.value()));
            }
        });
        Component component = componentRegistry.component(componentName).orElseThrow(() -> new TemplateException("missing component, name={}, template={}", element.name(), resource.path()));
        for (ComponentAttribute<?> attribute : component.attributes().values()) {
            if (!attributeExpressions.containsKey(attribute.name())) {
                attributeExpressions.put(attribute.name(), new ConstantExpression(attribute.defaultValue()));
            }
        }
        List<Segment> children = Lists.newArrayList();
        for (Node child : element.children()) {
            children.add(segment(child));
        }
        return new ComponentSegment(component, children.isEmpty() ? new EmptyChildren() : new ChildrenImpl(children), attributeExpressions);
    }

    private boolean isMSComment(Comment comment) {
        String html = comment.outerHtml();
        return html.startsWith("<!--[if") || html.startsWith("<!--<![");
    }

    private Expression parseExpression(Element element, String content, String source) {
        return templateModel.add(content, null, element.row(), element.column(), source);
    }

    private boolean isSelfClosedElement(String tagName) {
        return SELF_CLOSED_ELEMENTS.contains(tagName.toLowerCase(Locale.ENGLISH));
    }

    private boolean isBoolAttribute(String attributeName) {
        return BOOL_ATTRIBUTES.contains(attributeName.toLowerCase(Locale.ENGLISH));
    }
}
