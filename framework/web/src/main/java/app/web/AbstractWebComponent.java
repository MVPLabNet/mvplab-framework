package app.web;

import app.ApplicationException;
import app.template.AbstractComponent;
import app.template.Children;
import app.template.Component;
import app.template.ComponentAttribute;
import app.template.Attributes;
import app.template.Script;
import app.template.StringAttribute;
import app.template.StyleSheet;
import app.template.Template;
import app.template.TemplateEngine;
import app.template.TemplateException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author chi
 */
public abstract class AbstractWebComponent extends AbstractComponent {
    private final String templatePath;
    private TemplateEngine templateEngine;

    public AbstractWebComponent(String name, String templatePath) {
        this(name, templatePath, merge(ImmutableList.of()));
    }

    public AbstractWebComponent(String name, String templatePath, List<ComponentAttribute<?>> attributes) {
        super(name, merge(attributes));
        this.templatePath = templatePath;
    }

    private static List<ComponentAttribute<?>> merge(List<ComponentAttribute<?>> contributes) {
        List<ComponentAttribute<?>> mergedAttributes = Lists.newArrayList(contributes);
        Optional<ComponentAttribute<?>> idAttributeOptional = contributes.stream().filter(item -> item.name().equals("id")).findAny();
        if (!idAttributeOptional.isPresent()) {
            mergedAttributes.add(new StringAttribute("id", UUID.randomUUID().toString()));
        }

        Optional<ComponentAttribute<?>> classAttributeOptional = contributes.stream().filter(item -> item.name().equals("class")).findAny();
        if (!classAttributeOptional.isPresent()) {
            mergedAttributes.add(new StringAttribute("className", ""));
        }

        Optional<ComponentAttribute<?>> styleAttributeOptional = contributes.stream().filter(item -> item.name().equals("style")).findAny();
        if (!styleAttributeOptional.isPresent()) {
            mergedAttributes.add(new StringAttribute("style", ""));
        }

        Optional<ComponentAttribute<?>> titleAttributeOptional = contributes.stream().filter(item -> item.name().equals("title")).findAny();
        if (!titleAttributeOptional.isPresent()) {
            mergedAttributes.add(new StringAttribute("title", ""));
        }
        return mergedAttributes;
    }


    protected String templatePath() {
        if (templatePath == null) {
            throw new ApplicationException("missing template path, name={}", name());
        }
        return templatePath;
    }

    protected TemplateEngine templateEngine() {
        if (templateEngine == null) {
            throw new ApplicationException("missing template engine, name={}", name());
        }
        return templateEngine;
    }

    @Override
    public List<Component> refs() {
        return template().refs();
    }

    @Override
    public List<Script> scripts() {
        return template().scripts();
    }

    @Override
    public List<StyleSheet> styles() {
        return template().styles();
    }

    public AbstractWebComponent setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        return this;
    }

    protected Template template() {
        String templatePath = templatePath();
        return templateEngine().template(templatePath, true).orElseThrow(() -> new TemplateException("missing template, path={}", templatePath));
    }

    @Override
    public void output(Map<String, Object> bindings, Attributes attributes, Children children, OutputStream out) throws IOException {
        WebComponentBindings webComponentBindings = new WebComponentBindings(bindings);
        webComponentBindings.put("props", attributes);
        webComponentBindings.put("children", children);
        output(webComponentBindings, attributes, children, out);
    }

    protected abstract void output(WebComponentBindings bindings, Attributes attributes, Children children, OutputStream out) throws IOException;

}
