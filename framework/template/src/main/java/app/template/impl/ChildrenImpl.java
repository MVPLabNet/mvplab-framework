package app.template.impl;

import app.template.Children;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author chi
 */
public class ChildrenImpl implements Children {
    private final List<Segment> segments;

    public ChildrenImpl(List<Segment> segments) {
        this.segments = segments;
    }

    @Override
    public void output(Map<String, Object> bindings, OutputStream out) throws IOException {
        for (Segment segment : segments) {
            segment.output(bindings, out);
        }
    }
}
