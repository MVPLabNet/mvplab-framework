package app.swagger;

import app.AbstractModule;
import app.app.impl.exception.ExceptionResponse;
import app.test.AppExtension;
import app.test.Install;
import app.test.MockApp;
import org.glassfish.jersey.server.ContainerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.inject.Inject;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author chi
 */
@ExtendWith(AppExtension.class)
@Install({SwaggerModule.class, TestServiceModule.class})
class SwaggerModuleTest {
    @Inject
    MockApp app;

    @Test
    void configure() {
        assertNotNull(app);
    }

    @Test
    @SuppressWarnings("unchecked")
    void serviceSingleton() {
        AbstractModule module = app.module(TestServiceModule.class);
        TestService serviceImpl = module.require(TestService.class);
        ContainerResponse response = app.get("/api").execute();
        Map<String, Integer> entity = (Map<String, Integer>) response.getEntity();
        assertEquals(serviceImpl.hashCode(), (int) entity.get("hashCode"));
    }

    @Test
    void notFound() {
        ContainerResponse response = app.get("/api/not-found").execute();
        ExceptionResponse entity = (ExceptionResponse) response.getEntity();
        assertNotNull(entity.errorMessage);
    }

    @Test
    void defaultExceptionHandler() {
        ContainerResponse response = app.get("/api/exception").execute();
        ExceptionResponse entity = (ExceptionResponse) response.getEntity();
        assertNotNull(entity.errorMessage);
    }
}
