package app.template.impl.segment;

import app.template.Expression;
import app.template.impl.Segment;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author chi
 */
public class IfSegment extends CompositeSegment {
    private final Expression condition;

    public IfSegment(Expression condition) {
        this.condition = condition;
    }

    @Override
    public void output(Map<String, Object> bindings, OutputStream outputStream) throws IOException {
        Object value = condition.eval(bindings);
        if (Boolean.TRUE.equals(value)) {
            for (Segment segment : children()) {
                segment.output(bindings, outputStream);
            }
        }
    }
}
