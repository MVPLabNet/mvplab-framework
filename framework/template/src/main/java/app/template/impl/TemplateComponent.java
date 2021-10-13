package app.template.impl;

import app.ApplicationException;
import app.template.AbstractComponent;
import app.template.Attributes;
import app.template.Children;
import app.template.Component;
import app.template.ComponentAttribute;
import app.template.Script;
import app.template.StyleSheet;
import app.template.Template;
import app.template.TemplateEngine;
import app.template.TemplateException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author chi
 */
public class TemplateComponent extends AbstractComponent {
    private final String templatePath;
    TemplateEngine templateEngine;

    public TemplateComponent(String name, String templatePath) {
        this(name, templatePath, ImmutableList.of());
    }

    public TemplateComponent(String name, String templatePath, List<ComponentAttribute<?>> componentAttributes) {
        super(name, componentAttributes);
        this.templatePath = templatePath;
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

    @Override
    public void output(Map<String, Object> bindings, Attributes attributes, Children children, OutputStream out) throws IOException {
        Map<String, Object> componentBindings = Maps.newHashMap();
        componentBindings.putAll(bindings);
        componentBindings.putAll(attributes);
        template().output(componentBindings, out);
    }

    public TemplateComponent setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        return this;
    }

    protected Template template() {
        return templateEngine().template(templatePath, true).orElseThrow(() -> new TemplateException("missing template, path={}", templatePath));
    }
}
