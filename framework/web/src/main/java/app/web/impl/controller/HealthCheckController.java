package app.web.impl.controller;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

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
