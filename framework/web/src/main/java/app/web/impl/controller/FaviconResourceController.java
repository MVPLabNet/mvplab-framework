package app.web.impl.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

/**
 * @author chi
 */
@Path("/favicon.ico")
public class FaviconResourceController extends WebResourceController {
    @GET
    public Response get(@Context UriInfo uriInfo, @Context Request request) {
        return resource(uriInfo.getPath(), request);
    }
}
