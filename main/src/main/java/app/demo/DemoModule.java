package app.demo;

import app.database.DatabaseModule;
import app.demo.entity.DemoEntity;
import app.demo.service.DemoEntityService;
import app.demo.web.HelloWorldController;
import app.web.AbstractWebModule;

/**
 * @author chi
 */
public class DemoModule extends AbstractWebModule {
    public DemoModule() {
        super(DatabaseModule.class);
    }

    @Override
    protected void configure() {
        module(DatabaseModule.class)
            .entity(DemoEntity.class);

        bind(DemoEntityService.class);
        
        bindController(HelloWorldController.class);
    }
}
