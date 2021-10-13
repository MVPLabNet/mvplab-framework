package app.template;

import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author chi
 */
public interface Component {
    String name();

    default List<Component> refs() {
        return ImmutableList.of();
    }

    default List<Script> scripts() {
        return ImmutableList.of();
    }

    default List<StyleSheet> styles() {
        return ImmutableList.of();
    }

    Map<String, ComponentAttribute<?>> attributes();

    void output(Map<String, Object> bindings, Attributes attributes, Children children, OutputStream out) throws IOException;
}
