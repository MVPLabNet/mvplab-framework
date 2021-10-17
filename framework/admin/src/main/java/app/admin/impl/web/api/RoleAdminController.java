package app.admin.impl.web.api;

import app.AbstractModule;
import app.App;
import app.admin.impl.web.api.role.RoleGroup;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import jakarta.inject.Inject;
import java.util.List;

/**
 * @author chi
 */
@Path("/admin/api/role")
public class RoleAdminController {
    @Inject
    App app;

    @RolesAllowed("LIST")
    @GET
    public Response allDeclareRoles() {
        List<RoleGroup> roles = Lists.newArrayList();
        for (AbstractModule module : app.modules()) {
            List<String> declareRoles = module.declareRoles();
            if (!declareRoles.isEmpty()) {
                RoleGroup roleGroup = new RoleGroup();
                roleGroup.name = module.getClass().getPackageName();
                roleGroup.roles = declareRoles;
                roles.add(roleGroup);
            }
        }
        return Response.ok(roles).build();
    }

    @RolesAllowed("LIST")
    @Path("/module/{moduleName}")
    @GET
    public Response moduleDeclareRoles(@PathParam("moduleName") String moduleName) {
        for (AbstractModule module : app.modules()) {
            if (module.getClass().getPackageName().equals(moduleName)) {
                return Response.ok(module.declareRoles()).build();
            }
        }
        return Response.ok(ImmutableList.of()).build();
    }
}
