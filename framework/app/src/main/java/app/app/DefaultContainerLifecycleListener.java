package app.app;

import app.util.exception.Errors;
import org.glassfish.jersey.inject.hk2.DelayedHk2InjectionManager;
import org.glassfish.jersey.inject.hk2.ImmediateHk2InjectionManager;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.spi.AbstractContainerLifecycleListener;
import org.glassfish.jersey.server.spi.Container;

/**
 * @author chi
 */
class DefaultContainerLifecycleListener extends AbstractContainerLifecycleListener {
    final JerseyApp app;

    DefaultContainerLifecycleListener(JerseyApp app) {
        this.app = app;
    }

    @Override
    public void onStartup(Container container) {
        InjectionManager injectionManager = container.getApplicationHandler().getInjectionManager();
        if (injectionManager instanceof ImmediateHk2InjectionManager) {
            app.serviceLocator = ((ImmediateHk2InjectionManager) injectionManager).getServiceLocator();
        } else if (injectionManager instanceof DelayedHk2InjectionManager) {
            app.serviceLocator = ((DelayedHk2InjectionManager) injectionManager).getServiceLocator();
        } else {
            throw Errors.internalError("unknown injection manager");
        }
        app.onStartup();
    }

    @Override
    public void onShutdown(Container container) {
        app.onShutdown();
    }
}
