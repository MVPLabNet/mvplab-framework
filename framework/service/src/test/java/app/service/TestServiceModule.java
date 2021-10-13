package app.service;

import app.AbstractModule;

/**
 * @author chi
 */
public class TestServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bindService(TestService.class, TestServiceImpl.class);
    }
}
