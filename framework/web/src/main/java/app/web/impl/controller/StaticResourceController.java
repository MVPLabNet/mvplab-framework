package app.web.impl.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author chi
 */
@Path("/static/{s:.+}")
public class StaticResourceController extends WebResourceController {
    @GET
    public Response get(@Context UriInfo uriInfo) {
        return resource(uriInfo.getPath());
    }
}
