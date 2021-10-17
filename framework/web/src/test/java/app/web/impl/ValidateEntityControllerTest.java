package app.web.impl;

import app.test.AppExtension;
import app.test.Install;
import app.test.MockApp;
import app.web.AbstractWebModule;
import app.web.WebModule;
import org.glassfish.jersey.server.ContainerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author chi
 */
@ExtendWith(AppExtension.class)
@Install({WebModule.class, ValidateEntityControllerTest.TestModule.class})
public class ValidateEntityControllerTest {
    @Inject
    MockApp app;

    @Test
    void request() {
        TestRequest request = new TestRequest();
        ContainerResponse response = app.put("/test").setEntity(request).execute();
        assertEquals(400, response.getStatus());
    }

    public static class TestModule extends AbstractWebModule {
        @Override
        protected void configure() {
            web().controller(TestController.class);
        }
    }

    @Path("/test")
    public static class TestController {
        @PUT
        public TestResponse request(@Valid TestRequest request) {
            TestResponse testResponse = new TestResponse();
            testResponse.response = request.request;
            return testResponse;
        }
    }

    public static class TestRequest {
        @NotNull
        public String request;
    }

    public static class TestResponse {
        public String response;
    }
}
