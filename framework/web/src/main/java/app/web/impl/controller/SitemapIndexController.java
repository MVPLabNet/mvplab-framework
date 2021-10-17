package app.web.impl.controller;

import app.resource.Resource;
import app.util.exception.Errors;
import app.web.AbstractWebController;
import app.web.impl.SitemapService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.util.Optional;

/**
 * @author chi
 */
@Path("/sitemap.xml")
public class SitemapIndexController extends AbstractWebController {
    @Inject
    SitemapService sitemapService;

    @Inject
    UriInfo uriInfo;

    @GET
    public Response index() {
        String resourcePath = uriInfo.getPath();
        Optional<Resource> sitemap = sitemapService.sitemap(resourcePath);
        if (sitemap.isEmpty()) {
            sitemapService.build();
            sitemap = sitemapService.sitemap(resourcePath);
            if (sitemap.isEmpty()) {
                throw Errors.notFound("not found, path={}", uriInfo.getPath());
            }
        }
        return Response.ok(sitemap.get())
            .type("text/xml").build();
    }
}
