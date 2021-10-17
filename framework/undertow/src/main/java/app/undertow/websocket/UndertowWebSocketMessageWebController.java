package app.undertow.websocket;

import app.message.MessagePublisher;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * @author chi
 */
@Path("/api/web/websocket")
public class UndertowWebSocketMessageWebController {
    @Inject
    MessagePublisher<WebSocketMessage> publisher;

    @Path("/message")
    @POST
    public Response send(WebSocketMessage message) {
        publisher.publish(message);
        return Response.ok().type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
