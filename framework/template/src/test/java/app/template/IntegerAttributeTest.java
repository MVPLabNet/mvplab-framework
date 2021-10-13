package app.template;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author chi
 */
public class IntegerAttributeTest {
    @Test
    public void value() {
        IntegerAttribute attribute = new IntegerAttribute("test", 1);
        assertEquals(1, (int) attribute.value(ImmutableMap.of()));
        assertEquals(1, (int) attribute.value(Collections.singletonMap("test", 1)));
    }
}