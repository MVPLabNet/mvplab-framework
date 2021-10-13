package app.admin.impl.web;

import app.admin.ConsoleBundle;
import app.admin.impl.service.Console;
import app.resource.CombinedResource;
import app.resource.Resource;
import app.util.MediaTypes;
import app.web.WebRoot;
import com.google.common.collect.Lists;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author chia
 */
@Path("/admin/static")
public class AdminStaticResourceController {
    @Inject
    Console console;

    @Inject
    WebRoot webRoot;

    @Path("/{s:.+}")
    @GET
    @Consumes
    public Response get(@Context UriInfo uriInfo, @Context Request request) throws IOException {
        String path = uriInfo.getPath();
        Optional<ConsoleBundle> consoleBundleOptional = console.findByScriptFile(path);
        Resource resource;
        if (consoleBundleOptional.isPresent()) {
            ConsoleBundle consoleBundle = consoleBundleOptional.get();
            resource = new CombinedResource(path, resourcePaths(consoleBundle), webRoot);
        } else {
            Optional<Resource> optional = webRoot.get(path);
            if (!optional.isPresent()) {
                throw new NotFoundException(String.format("missing resource, path=%s", path));
            }
            resource = optional.get();
        }
        EntityTag eTag = new EntityTag(resource.hash());
        Response.ResponseBuilder builder = request.evaluatePreconditions(eTag);
        if (builder != null) {
            return builder.build();
        }
        builder = Response.ok(resource).type(MediaTypes.getMediaType(path));
        builder.tag(eTag);
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMustRevalidate(true);
        builder.cacheControl(cacheControl);
        return builder.build();
    }

    private List<String> resourcePaths(ConsoleBundle consoleBundle) {
        List<String> resourcePaths = Lists.newArrayList();
        resourcePaths.add(consoleBundle.scriptFile);
        resourcePaths.addAll(consoleBundle.scriptFiles);
        return resourcePaths;
    }
}
