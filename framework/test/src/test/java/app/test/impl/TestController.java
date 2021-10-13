package app.test.impl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

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
