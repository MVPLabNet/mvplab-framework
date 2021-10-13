package app;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * @author chi
 */
public class GraphTest {
    @Test
    void test() {
        MutableGraph<String> graph = GraphBuilder.directed().allowsSelfLoops(false)
            .build();

        graph.addNode("1");
        graph.addNode("2");
        graph.addNode("3");

        graph.putEdge("1", "2");
        graph.putEdge("2", "3");

        Set<String> nodes = graph.adjacentNodes("1");
        System.out.println(nodes);
    }
}
