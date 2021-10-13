package app.database.impl;

import app.AbstractModule;
import app.database.DatabaseModule;

/**
 * @author chi
 */
public class TestModule extends AbstractModule {
    public TestModule() {
        super(DatabaseModule.class);
    }

    @Override
    protected void configure() {
        module(DatabaseModule.class)
            .entity(TestEntity.class);

        bind(TestService.class);
    }
}
