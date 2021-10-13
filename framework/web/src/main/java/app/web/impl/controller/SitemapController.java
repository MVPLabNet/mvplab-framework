package app.web.impl.controller;

import app.resource.Resource;
import app.util.exception.Errors;
import app.web.AbstractWebController;
import app.web.impl.SitemapService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;

/**
 * @author chi
 */
@Path("/sitemap")
public class SitemapController extends AbstractWebController {
    @Inject
    SitemapService sitemapService;

    @Inject
    UriInfo uriInfo;

    @Path("/{s:.+}")
    @GET
    public Response index() {
        String resourcePath = uriInfo.getPath();
        Optional<Resource> sitemap = sitemapService.sitemap(resourcePath);
        if (sitemap.isEmpty()) {
            throw Errors.notFound("not found, path={}", uriInfo.getPath());
        }
        return Response.ok(sitemap.get())
            .type("text/xml").build();
    }
}
