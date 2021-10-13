package app.admin;

import app.AbstractModule;
import app.web.AbstractWebModule;
import com.google.common.collect.ObjectArrays;

/**
 * @author chi
 */
public abstract class AbstractAdminModule extends AbstractWebModule {
    protected AbstractAdminModule() {
        super(AdminModule.class);
    }

    @SafeVarargs
    protected AbstractAdminModule(Class<? extends AbstractModule>... dependencies) {
        super(ObjectArrays.concat(AdminModule.class, dependencies));
    }

    protected AdminConfig admin() {
        return module(AdminModule.class);
    }
}
