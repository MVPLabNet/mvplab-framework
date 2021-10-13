package app;

import app.resource.Resource;
import app.util.exception.Errors;
import app.util.i18n.ResourceMessageBundle;
import com.google.common.collect.Lists;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;


/**
 * @author chi
 */
public abstract class AbstractModule {
    private final Class<? extends AbstractModule>[] dependencies;
    private app.App app;

    @SuppressWarnings("unchecked")
    protected AbstractModule() {
        dependencies = new Class[0];
    }

    @SafeVarargs
    protected AbstractModule(Class<? extends AbstractModule>... dependencies) {
        this.dependencies = dependencies;
    }

    public void configure(app.App app) {
        if (this.app != null) {
            throw Errors.internalError("module configured, type={}", getClass().getCanonicalName());
        }
        this.app = app;
        configure();
    }

    protected abstract void configure();

    public Class<? extends AbstractModule>[] dependencies() {
        return dependencies;
    }

    public List<String> declareRoles() {
        return Lists.newArrayList("LIST", "GET", "CREATE", "UPDATE", "DELETE", "AUDIT");
    }

    protected void onStartup(Runnable hook) {
        binder().addStartupHook(hook);
    }

    protected void onReady(Runnable hook) {
        binder().addReadyHook(hook);
    }

    protected void onShutdown(Runnable hook) {
        binder().addShutdownHook(hook);
    }

    protected <T> T options(String name, Class<T> optionClass) {
        return app().profile().options(name, optionClass);
    }

    public <T> T require(Class<T> type, Annotation qualifier) {
        return require((Type) type, qualifier);
    }

    public <T> T require(Type type, Annotation qualifier) {
        T service = app().require(type, qualifier);
        if (service == null) {
            throw Errors.internalError("missing binding, type={}", type);
        }
        return service;
    }

    @SuppressWarnings("unchecked")
    public <T> T require(Type type) {
        T service = app().require(type, null);
        if (service == null) {
            throw Errors.internalError("missing binding, type={}", type);
        }
        return service;
    }

    public <T> T require(Class<T> type) {
        return require((Type) type);
    }

    @SuppressWarnings("unchecked")
    protected <K, T extends AbstractModule & Configurable<K>> K module(Class<T> type) {
        return app.config(this, type);
    }

    protected <T> app.AnnotatedBindingBuilder<T> bind(Class<T> fromType) {
        return binder().bind(fromType);
    }

    protected <T> app.AnnotatedBindingBuilder<T> bind(Type fromType) {
        return binder().bind(fromType);
    }

    protected void bindInterceptor(Class<? extends Annotation> annotationClass, MethodInterceptor interceptor) {
        binder().bindInterceptor(annotationClass, interceptor);
    }

    protected <T> void bindController(Class<T> controllerClass) {
        binder().bind(controllerClass);
        app.register(controllerClass);
    }

    protected <T> void bindService(Class<T> serviceClass, Class<? extends T> serviceImplClass) {
        binder().bind(serviceClass).to(serviceImplClass);
        if (app.isAPIEnabled()) {
            app.register(new app.ServiceDelegateBuilder<>(serviceClass).build());
        }
    }

    protected void message(String path) {
        app.message().bind(path, new ResourceMessageBundle(path, app.resource(), app.language()));
    }

    protected Resource resource(String path) {
        return app.resource().get(path).orElseThrow(() -> Errors.internalError("missing resource, path={}", path));
    }

    protected <T> T requestInjection(T instance) {
        binder().requestInjection(instance);
        return instance;
    }

    protected app.App app() {
        return app;
    }

    private Binder binder() {
        return this.app().binder(this);
    }
}