package app.util.type;

/**
 * @author chi
 */
public interface ObjectConstructor<T> {
    T newInstance(Object... args);
}
