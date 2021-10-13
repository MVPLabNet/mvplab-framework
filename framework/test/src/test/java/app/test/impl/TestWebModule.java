package app.test.impl;

import app.web.AbstractWebModule;

/**
 * @author chi
 */
public class TestWebModule extends AbstractWebModule {
    @Override
    protected void configure() {
        web().controller(TestController.class);
    }
}
