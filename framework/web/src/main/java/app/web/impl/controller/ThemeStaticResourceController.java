package app.web.impl.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

/**
 * @author chi
 */
@Path("/{theme}/static/{s:.+}")
public class ThemeStaticResourceController extends WebResourceController {
    @GET
    public Response get(@Context UriInfo uriInfo) {
        return resource(uriInfo.getPath());
    }
}
