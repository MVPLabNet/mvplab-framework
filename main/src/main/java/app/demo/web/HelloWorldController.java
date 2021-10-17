package app.demo.web;

import app.demo.service.DemoEntityService;
import app.util.JSON;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/hello")
public class HelloWorldController {
    @Inject
    DemoEntityService demoEntityService;

    @GET
    public String hello() {
        return JSON.toJSON(demoEntityService.get());
    }
}
