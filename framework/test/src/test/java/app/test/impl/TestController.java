package app.test.impl;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * @author chi
 */
@Path("/test")
public class TestController {
    @GET
    public Response get() {
        return Response.ok().build();
    }
}
