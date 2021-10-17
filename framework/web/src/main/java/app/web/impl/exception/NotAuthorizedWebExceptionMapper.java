package app.web.impl.exception;

import jakarta.annotation.Priority;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.process.internal.RequestScoped;

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
