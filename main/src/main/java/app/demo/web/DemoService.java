package app.demo.web;

import app.demo.web.user.RegisterUserResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.Optional;

/**
 * @author chi
 */
@Path("/web/api/service")
public interface DemoService {
    @GET
    Optional<RegisterUserResponse> get();
}
