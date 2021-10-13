package app.util.type;

import app.ApplicationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author chi
 */
public class ConstructorsTest {
    @Test
    public void newInstance() {
        ObjectConstructor<TestObject> constructor = Constructors.of(TestObject.class);
        assertNotNull(constructor.newInstance());
    }

    @Test
    public void failedToGetConstructor() {
        assertThrows(ApplicationException.class, () -> {
            Constructors.of(TestObject.class, String.class);
        });
    }

    @Test
    public void failedToCreateInstance() {
        assertThrows(ApplicationException.class, () -> {
            Constructors.of(TestObject.class).newInstance("xxx");
        });
    }

    static class TestObject {
    }
}