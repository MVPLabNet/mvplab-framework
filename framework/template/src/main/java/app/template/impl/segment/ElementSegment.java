package app.template.impl.segment;

import app.template.impl.Segment;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author chi
 */
public class ElementSegment extends CompositeSegment {
    private final List<Segment> start;
    private final List<Segment> end;

    public ElementSegment(List<Segment> start, List<Segment> end) {
        this.start = join(start);
        this.end = join(end);
    }

    private List<Segment> join(List<Segment> segments) {
        if (segments.isEmpty()) {
            return Collections.emptyList();
        }
        List<Segment> builder = new ArrayList<>();
        Segment last = segments.get(0);
        builder.add(last);
        for (int i = 1; i < segments.size(); i++) {
            Segment current = segments.get(i);
            if (last instanceof StaticSegment && current instanceof StaticSegment) {
                StaticSegment joined = ((StaticSegment) last).join((StaticSegment) current);
                builder.set(builder.size() - 1, joined);
                last = joined;
            } else {
                last = current;
                builder.add(current);
            }
        }
        return builder;
    }


    @Override
    public void output(Map<String, Object> bindings, OutputStream outputStream) throws IOException {
        for (Segment segment : start) {
            segment.output(bindings, outputStream);
        }

        for (Segment segment : children()) {
            segment.output(bindings, outputStream);
        }

        for (Segment segment : end) {
            segment.output(bindings, outputStream);
        }
    }
}
