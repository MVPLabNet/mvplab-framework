package app.demo.web;

import app.demo.service.DemoEntityService;
import app.demo.web.user.RegisterUserRequest;
import app.demo.web.user.RegisterUserResponse;
import app.util.JSON;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import java.util.Optional;

@Path("/web/api/hello")
public class HelloWorldController {
    @Inject
    DemoEntityService demoEntityService;

    @GET
    public String hello() {
        return JSON.toJSON(demoEntityService.get());
    }

    @GET
    @Path("/register")
    public RegisterUserResponse register(@Valid RegisterUserRequest request) {
        return new RegisterUserResponse();
    }

    @GET
    @Path("/{name}")
    public Optional<RegisterUserResponse> name(@PathParam("name") String name) {
        return Optional.of(new RegisterUserResponse());
    }
}
