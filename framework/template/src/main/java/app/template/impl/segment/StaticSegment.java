package app.template.impl.segment;

import app.template.impl.Segment;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author chi
 */
public class StaticSegment implements Segment {
    private final byte[] content;

    public StaticSegment(String content) {
        this.content = content.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void output(Map<String, Object> bindings, OutputStream writer) throws IOException {
        writer.write(content);
    }

    public StaticSegment join(StaticSegment segment) {
        return new StaticSegment(new String(content, StandardCharsets.UTF_8) + new String(segment.content, StandardCharsets.UTF_8));
    }
}
