package app.admin.impl.web.api;

import app.web.WebOptions;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
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
