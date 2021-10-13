package app.test;

import app.AbstractModule;
import app.database.DatabaseModule;
import app.test.impl.Test2Service;

/**
 * @author chi
 */
public class Test2Module extends AbstractModule {
    public Test2Module() {
        super(DatabaseModule.class);
    }

    @Override
    protected void configure() {
        bind(Test2Service.class);
    }
}
