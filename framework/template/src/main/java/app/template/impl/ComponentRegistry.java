package app.template.impl;

import app.template.Component;
import app.template.Template;
import app.template.TemplateEngine;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author chi
 */
public class ComponentRegistry {
    private final Logger logger = LoggerFactory.getLogger(ComponentRegistry.class);
    private volatile boolean autoComponentEnabled = true;
    private final TemplateEngine templateEngine;
    private final Map<String, Component> components = Maps.newConcurrentMap();

    public ComponentRegistry(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void setAutoComponentEnabled(boolean autoComponentEnabled) {
        this.autoComponentEnabled = autoComponentEnabled;
    }

    public void put(String name, Component component) {
        String componentName = name.toLowerCase();
        if (components.containsKey(componentName)) {
            logger.info("override component, name={}, component={}, original={}", componentName, component.getClass().getCanonicalName(), components.get(componentName).getClass().getCanonicalName());
        }
        components.put(componentName, component);
    }

    public Optional<Component> component(String name) {
        String componentName = name.toLowerCase();
        Component component = components.get(componentName);
        if (component == null && autoComponentEnabled) {
            String templatePath = "component/" + name + "/" + name + ".html";
            Optional<Template> template = templateEngine.template(templatePath);
            if (template.isPresent()) {
                templateEngine.addComponent(new TemplateComponent(name, templatePath));
                component = components.get(name);
            }
        }
        return Optional.ofNullable(component);
    }

    public List<Component> components() {
        return ImmutableList.copyOf(components.values());
    }
}
