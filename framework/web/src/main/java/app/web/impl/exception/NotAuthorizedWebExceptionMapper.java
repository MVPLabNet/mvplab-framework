package app.web.impl.exception;

import org.glassfish.jersey.process.internal.RequestScoped;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Response;
import java.net.URI;

@RequestScoped
@Priority(Priorities.USER - 1000)
public class NotAuthorizedWebExceptionMapper extends AbstractWebApplicationExceptionMapper<NotAuthorizedException> {
    public NotAuthorizedWebExceptionMapper() {
        super(Response.Status.UNAUTHORIZED);
    }

    @Override
    public Response toResponse(NotAuthorizedException e) {
        return Response.temporaryRedirect(URI.create("/user/login")).build();
    }
}