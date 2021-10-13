package app.template.impl.segment;

import app.template.Expression;
import app.template.impl.Segment;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author chi
 */
public class AttributeExpressionSegment implements Segment {
    private final Expression expression;
    private final Escaper escaper;

    public AttributeExpressionSegment(Expression expression) {
        this.expression = expression;
        escaper = Escapers.builder().addEscape('\"', "\\\"").addEscape('\'', "\\\'").build();
    }

    @Override
    public void output(Map<String, Object> bindings, OutputStream writer) throws IOException {
        Object value = expression.eval(bindings);
        String content = value == null ? "" : escaper.escape(String.valueOf(value));
        writer.write(content.getBytes(StandardCharsets.UTF_8));
    }
}
