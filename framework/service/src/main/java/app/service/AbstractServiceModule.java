package app.service;

import app.AbstractModule;
import com.google.common.collect.ObjectArrays;

/**
 * @author chi
 */
public abstract class AbstractServiceModule extends AbstractModule {
    protected AbstractServiceModule() {
        super(ServiceModule.class);
    }

    @SafeVarargs
    protected AbstractServiceModule(Class<? extends AbstractModule>... dependencies) {
        super(ObjectArrays.concat(ServiceModule.class, dependencies));
    }

    protected ServiceConfig api() {
        return module(ServiceModule.class);
    }
}
