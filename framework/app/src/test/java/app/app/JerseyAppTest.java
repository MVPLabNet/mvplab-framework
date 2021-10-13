package app.app;

import app.AbstractModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.ws.rs.WebApplicationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author chi
 */
public class JerseyAppTest {
    @Test
    void dependencies() {
        JerseyApp app = new JerseyApp();
        app.install(new TestModule1());
        app.install(new TestModule2());
        app.install(new TestModule3());
        app.install(new TestModule4());

        List<Class<? extends AbstractModule>> dependencies = app.recursiveDependencies(TestModule2.class);
        assertEquals(1, dependencies.size());
    }

    @Test
    void install() {
        JerseyApp registry = new JerseyApp();
        registry.install(new TestModule1());
        registry.install(new TestModule2());
        registry.install(new TestModule3());
        registry.install(new TestModule4());

        registry.validate();
    }

    @Test
    void validate() {
        Assertions.assertThrows(WebApplicationException.class, () -> {
            JerseyApp registry = new JerseyApp();
            registry.install(new TestModule5());
            registry.install(new TestModule6());
            registry.validate();
        });
    }

    static class TestModule1 extends AbstractModule {
        public TestModule1() {
        }

        public TestModule1(Class<? extends AbstractModule>... dependencies) {
            super(dependencies);
        }

        @Override
        protected void configure() {
        }
    }

    static class TestModule2 extends TestModule1 {
        @Override
        protected void configure() {

        }
    }

    static class TestModule3 extends AbstractModule {
        @Override
        protected void configure() {
        }
    }


    static class TestModule4 extends TestModule1 {
        public TestModule4() {
            super(TestModule3.class);
        }

        @Override
        protected void configure() {
        }
    }

    static class TestModule5 extends AbstractModule {
        public TestModule5() {
            super(TestModule6.class);
        }

        @Override
        protected void configure() {

        }
    }

    static class TestModule6 extends AbstractModule {
        public TestModule6() {
            super(TestModule5.class);
        }

        @Override
        protected void configure() {

        }
    }
}