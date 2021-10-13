package app.test;

import app.AbstractModule;
import app.test.impl.TestService;

/**
 * @author chi
 */
public class TestModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TestService.class);
    }
}
