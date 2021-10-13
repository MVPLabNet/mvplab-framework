package app.template.impl;

import app.resource.StringResource;
import app.template.Node;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author chi
 */
class HTMLParserTest {
    @Test
    void parse() throws IOException {
        HTMLParser htmlParser = new HTMLParser(new StringResource("test.html", "<!doctype html><html><Div>some</Div><!-- some --></html>"));
        List<Node> elements = htmlParser.parse();
        assertEquals(2, elements.size());
    }
}