package app.undertow.websocket;

import app.message.MessagePublisher;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
