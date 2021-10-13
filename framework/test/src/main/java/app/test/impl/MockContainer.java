package app.test.impl;

import app.test.MockApp;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spi.Container;

/**
 * @author chi
 */
public class MockContainer implements Container {
    private final ResourceConfig resourceConfig = new ResourceConfig();
    private final ApplicationHandler applicationHandler;

    public MockContainer(MockApp app) {
        applicationHandler = new ApplicationHandler(app);
    }

    @Override
    public ResourceConfig getConfiguration() {
        return resourceConfig;
    }

    @Override
    public ApplicationHandler getApplicationHandler() {
        return applicationHandler;
    }

    @Override
    public void reload() {
    }

    @Override
    public void reload(ResourceConfig configuration) {
    }
}
