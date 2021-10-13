package app.template;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author chi
 */
class TemplateFunctionsTest {
    @Test
    void format() {
        TemplateFunctions templateFunctions = new TemplateFunctions();
        assertEquals("1.0", templateFunctions.format(1, "##0.0##"));
    }
}