package app.undertow.websocket;

import app.message.MessageHandler;

import javax.inject.Inject;

/**
 * @author chi
 */
public class WebSocketMessageHandler implements MessageHandler<WebSocketMessage> {
    @Inject
    WebSocketService webSocketService;

    @Override
    public void handle(WebSocketMessage message) {
        webSocketService.onMessage(message);
    }
}
