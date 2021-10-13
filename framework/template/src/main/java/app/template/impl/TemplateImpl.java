package app.template.impl;

import app.template.Component;
import app.template.Script;
import app.template.StyleSheet;
import app.template.Template;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author chi
 */
public class TemplateImpl implements Template {
    private final String path;
    private final List<Segment> segments;
    private final List<Component> components;
    private final List<Script> scripts;
    private final List<StyleSheet> styles;

    public TemplateImpl(String path, List<Segment> segments, List<Component> components, List<Script> scripts, List<StyleSheet> styles) {
        this.path = path;
        this.segments = segments;
        this.components = components;
        this.scripts = scripts;
        this.styles = styles;
    }

    @Override
    public void output(Map<String, Object> bindings, OutputStream writer) throws IOException {
        for (Segment segment : segments) {
            segment.output(bindings, writer);
        }
    }

    public String path() {
        return path;
    }

    @Override
    public List<Script> scripts() {
        return scripts;
    }

    @Override
    public List<Component> refs() {
        return components;
    }

    @Override
    public List<StyleSheet> styles() {
        return styles;
    }
}
