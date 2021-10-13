package app.util.type;

import app.ApplicationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author chi
 */
public class ClassValidatorTest {

    @Test
    public void allowType() {
        ClassValidator classValidator = new ClassValidator(String.class);
        classValidator.allow(String.class);
        assertTrue(classValidator.validate());
    }

    @Test
    public void allowArray() {
        assertThrows(ApplicationException.class, () -> {
            ClassValidator classValidator = new ClassValidator(HasArray.class);
            classValidator.allow(String.class);
            classValidator.validate();
        });
    }

    @Test
    public void methodAllowed() {
        assertThrows(ApplicationException.class, () -> {
            ClassValidator classValidator = new ClassValidator(HasMethod.class);
            classValidator.validate();
        });
    }

    @Test
    public void genericAllowed() {
        ClassValidator classValidator = new ClassValidator(HasGeneric.class);
        classValidator.allowGeneric();
        classValidator.allow(List.class);
        classValidator.allow(String.class);
        assertTrue(classValidator.validate());
    }

    public static class HasArray {
        public String[] array;
    }

    public static class HasMethod {
        public void method() {
        }
    }

    public static class HasGeneric {
        public List<String> list;
    }
}