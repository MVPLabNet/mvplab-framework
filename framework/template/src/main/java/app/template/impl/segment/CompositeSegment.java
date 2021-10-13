package app.template.impl.segment;

import app.template.impl.Segment;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author chi
 */
public class CompositeSegment implements Segment {
    private final List<Segment> children = new ArrayList<>();

    public void addChild(Segment child) {
        if (!children.isEmpty()) {
            Segment last = children.get(children.size() - 1);
            if (last instanceof StaticSegment && child instanceof StaticSegment) {
                StaticSegment joined = ((StaticSegment) last).join((StaticSegment) child);
                children.set(children.size() - 1, joined);
                return;
            }
        }
        children.add(child);
    }

    public List<Segment> children() {
        return children;
    }


    @Override
    public void output(Map<String, Object> bindings, OutputStream outputStream) throws IOException {
        for (Segment child : children) {
            child.output(bindings, outputStream);
        }
    }
}
