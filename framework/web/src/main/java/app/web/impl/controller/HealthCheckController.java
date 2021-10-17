package app.web.impl.controller;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * @author chi
 */
@Path("/health-check")
public class HealthCheckController {
    @GET
    public Response check() {
        return Response.ok().build();
    }
}
