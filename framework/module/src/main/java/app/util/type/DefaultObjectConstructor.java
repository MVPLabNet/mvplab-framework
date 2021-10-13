package app.util.type;

import app.ApplicationException;

import java.lang.reflect.Constructor;

/**
 * @author chi
 */
class DefaultObjectConstructor<T> implements ObjectConstructor<T> {
    private final Constructor<T> constructor;

    DefaultObjectConstructor(Constructor<T> constructor) {
        this.constructor = constructor;
    }

    @Override
    public T newInstance(Object... args) {
        try {
            return constructor.newInstance(args);
        } catch (Throwable e) {
            throw new ApplicationException("failed to create new instance, constructor={}, args={}", constructor.getParameterTypes(), args, e);
        }
    }
}
