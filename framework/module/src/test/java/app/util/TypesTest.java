package app.util;

import app.util.type.Types;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author chi
 */
public class TypesTest {
    @Test
    public void className() {
        Assertions.assertEquals("java.lang.String", Types.className(String.class));
        assertEquals("java.util.List", Types.className(Types.generic(List.class, String.class)));
    }

    @Test
    public void variableType() throws NoSuchMethodException {
        Type list = Types.generic(List.class, String.class);
        Method method = List.class.getMethod("get", int.class);
        assertEquals(String.class, Types.actualType(list, method.getGenericReturnType()).orElse(null));

        Type map = Types.generic(Map.class, String.class, Object.class);
        Method get = Map.class.getMethod("get", Object.class);
        assertEquals(Object.class, Types.actualType(map, get.getGenericReturnType()).orElse(null));
    }

}