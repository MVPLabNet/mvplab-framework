package app.template.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author chi
 */
class TemplateLiteralTest {
    @Test
    void parse() {
        TemplateLiteral parser = new TemplateLiteral("hello {{world}}");
        assertEquals("\"hello \"+world", parser.expr());
    }

    @Test
    void quote() {
        TemplateLiteral parser = new TemplateLiteral("\"hello\" {{world}}");
        assertEquals("\"\\\"hello\\\" \"+world", parser.expr());
    }

    @Test
    void escape() {
        TemplateLiteral parser = new TemplateLiteral("\"hello\n\t\" {{world}}");
        assertEquals("\"\\\"hello\\\" \"+world", parser.expr());
    }

    @Test
    void tokens() {
        String text = "The following list are {{items.size()}} best {{map.fields().get(\"key\")}} gift ideas for {{map.fields().get(\"key\")}}";
        TemplateLiteral literal = new TemplateLiteral(text);
        assertEquals(6, literal.tokens().size());
    }
}