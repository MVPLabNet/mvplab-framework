package app.util.collection;

import app.ApplicationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author chi
 */
public class RegistryTest {
    @Test
    public void overrideAllowed() {
        Registry<String, String> registry = new Registry<>();
        registry.allowOverride();
        registry.put("1", "2");
        registry.put("1", "3");

        assertEquals("3", registry.get("1").orElseThrow(RuntimeException::new));
    }

    @Test
    public void overrideNotAllowed() {
        assertThrows(ApplicationException.class, () -> {
            Registry<String, String> registry = new Registry<>();
            registry.put("1", "2");
            registry.put("1", "3");
        });
    }

    @Test
    public void priority() {
        Registry<String, String> registry = new Registry<>();
        registry.allowOverride().withComparator(String::compareTo);
        registry.put("1", "2");
        registry.put("1", "3");
        assertEquals("2", registry.get("1").orElseThrow(RuntimeException::new));
    }
}