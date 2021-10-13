package app.template;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author chi
 */
public interface Template {
    void output(Map<String, Object> bindings, OutputStream out) throws IOException;

    List<StyleSheet> styles();

    List<Script> scripts();

    List<Component> refs();
}
