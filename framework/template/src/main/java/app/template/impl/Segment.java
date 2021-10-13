package app.template.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author chi
 */
public interface Segment {
    void output(Map<String, Object> bindings, OutputStream out) throws IOException;
}
