package app.swagger.web;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/openapi")
public class AcceptHeaderOpenApiResource extends BaseOpenApiResource {
    @Context
    Application app;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(hidden = true)
    public Response getOpenApiJson(@Context HttpHeaders headers,
                                   @Context UriInfo uriInfo) throws Exception {
        return super.getOpenApi(headers, app, uriInfo, "json");
    }

    @GET
    @Produces({"application/yaml"})
    @Operation(hidden = true)
    public Response getOpenApiYaml(@Context HttpHeaders headers,
                                   @Context UriInfo uriInfo) throws Exception {
        return super.getOpenApi(headers, app, uriInfo, "yaml");
    }
}
