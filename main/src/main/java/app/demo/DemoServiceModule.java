package app.demo;

import app.demo.web.DemoService;
import app.demo.web.DemoServiceImpl;
import app.service.AbstractServiceModule;

/**
 * @author chi
 */
public class DemoServiceModule extends AbstractServiceModule {
    public DemoServiceModule() {
    }

    @Override
    protected void configure() {
        bindService(DemoService.class, DemoServiceImpl.class);
    }
}
