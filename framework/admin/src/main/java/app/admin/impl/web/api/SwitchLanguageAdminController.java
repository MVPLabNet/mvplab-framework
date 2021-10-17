package app.admin.impl.web.api;

import app.web.WebOptions;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import java.io.IOException;

/**
 * @author chi
 */
@Path("/admin/api")
public class SwitchLanguageAdminController {
    @Inject
    WebOptions webOptions;

    @Path("/switch-language/{language}")
    @GET
    public Response get(@PathParam("language") String language) throws IOException {
        return Response.ok().cookie(new NewCookie(webOptions.cookie.language, language, "/", null, null, Integer.MAX_VALUE, false))
            .build();
    }
}
