package app.util.i18n;


import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author chi
 */
public class CompositeMessageBundleTest {
    @Test
    public void messageBundle() {
        CompositeMessageBundle compositeMessageBundle = new CompositeMessageBundle();
        compositeMessageBundle.bind("1.properties", new MapMessageBundle(Collections.emptyMap()));
        assertTrue(compositeMessageBundle.messageBundle("1.properties").isPresent());
    }

    @Test
    public void get() {
        CompositeMessageBundle compositeMessageBundle = new CompositeMessageBundle();
        compositeMessageBundle.bind("1.properties", new MapMessageBundle(Collections.singletonMap("name", "value")));
        Optional<String> message = compositeMessageBundle.get("name");
        assertEquals("value", message.orElse(null));
    }

    @Test
    public void keys() {
        CompositeMessageBundle compositeMessageBundle = new CompositeMessageBundle();
        compositeMessageBundle.bind("1.properties", new MapMessageBundle(Collections.singletonMap("name", "value")));
        assertEquals(1, compositeMessageBundle.keys().size());
    }
}