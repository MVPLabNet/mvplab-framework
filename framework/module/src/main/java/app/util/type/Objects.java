package app.util.type;

/**
 * @author chi
 */
public interface Objects {
    static boolean booleanValue(Object object) {
        return (Boolean) object;
    }

    static int intValue(Object object) {
        return (Integer) object;
    }

    static long longValue(Object object) {
        return (Long) object;
    }

    static short shortValue(Object object) {
        return (Short) object;
    }

    static char charValue(Object object) {
        return (Character) object;
    }

    static double doubleValue(Object object) {
        return (Double) object;
    }

    static float floatValue(Object object) {
        return (Float) object;
    }
}
