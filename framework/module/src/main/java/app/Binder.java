package app;

import org.aopalliance.intercept.MethodInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author chi
 */
public interface Binder {
    <T> AnnotatedBindingBuilder<T> bind(Type type);

    <T> AnnotatedBindingBuilder<T> bind(Class<T> type);

    <T> Binder requestInjection(T instance);

    void bindInterceptor(Class<? extends Annotation> annotationClass, MethodInterceptor interceptor);

    void addShutdownHook(Runnable handler);

    void addStartupHook(Runnable handler);

    void addReadyHook(Runnable handler);
}
