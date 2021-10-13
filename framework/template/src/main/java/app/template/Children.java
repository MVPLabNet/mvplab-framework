package app.template;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author chi
 */
public interface Children {
    class Empty implements Children {
        @Override
        public void output(Map<String, Object> bindings, OutputStream out) throws IOException {
        }
    }

    void output(Map<String, Object> bindings, OutputStream out) throws IOException;
}
