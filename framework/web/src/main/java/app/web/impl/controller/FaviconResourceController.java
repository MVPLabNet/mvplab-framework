package app.web.impl.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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
