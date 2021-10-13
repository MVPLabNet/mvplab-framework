package app.test.impl;

import app.test.AppExtension;
import app.test.Install;
import app.test.MockApp;
import app.web.WebModule;
import org.glassfish.jersey.server.ContainerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author chi
 */
@ExtendWith(AppExtension.class)
@Install({TestWebModule.class, WebModule.class})
class RequestBuilderImplTest {
    @Inject
    MockApp app;

    @Test
    void get() {
        ContainerResponse response = app.get("/test").execute();
        assertEquals(200, response.getStatus());
    }
}