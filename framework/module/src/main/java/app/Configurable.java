package app;


/**
 * @author chi
 */
public interface Configurable<T> {
    T configurator(AbstractModule module, Binder binder);
}
