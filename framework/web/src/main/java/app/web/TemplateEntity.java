package app.web;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * @author chi
 */
public class TemplateEntity {
    public final String path;
    public final Map<String, Object> bindings;

    public TemplateEntity(String path, Map<String, Object> bindings) {
        this.path = path;
        this.bindings = bindings;
    }

    public TemplateEntity(String path) {
        this(path, ImmutableMap.of());
    }
}
