package app.web;

import app.AbstractModule;
import com.google.common.collect.ObjectArrays;

/**
 * @author chi
 */
public abstract class AbstractWebModule extends AbstractModule {
    protected AbstractWebModule() {
        super(WebModule.class);
    }

    @SafeVarargs
    protected AbstractWebModule(Class<? extends AbstractModule>... dependencies) {
        super(ObjectArrays.concat(WebModule.class, dependencies));
    }

    protected WebConfig web() {
        return module(WebModule.class);
    }
}
