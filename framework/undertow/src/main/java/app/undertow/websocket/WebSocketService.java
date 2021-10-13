package app.undertow.websocket;

import app.message.MessagePublisher;
import app.util.JSON;
import app.util.exception.Errors;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.Cookies;
import io.undertow.util.Headers;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author chi
 */
public class WebSocketService {
    private final Map<String, WebSocketChannel> activeChannels = Maps.newConcurrentMap();
    @Inject
    MessagePublisher<WebSocketMessage> publisher;
    @Inject
    MessagePublisher<WebSocketConnectMessage> connectMessageMessagePublisher;
    @Inject
    MessagePublisher<WebSocketDisconnectMessage> disconnectMessageMessagePublisher;

    public void send(WebSocketMessage message) {
        String clientId = message.clientId;
        if (activeChannels.containsKey(clientId)) {
            WebSocketChannel channel = activeChannels.get(clientId);
            WebSockets.sendText(JSON.toJSON(message), channel, null);
        } else {
            publisher.publish(message);
        }
    }

    public void onMessage(WebSocketMessage message) {
        String clientId = message.clientId;
        if (activeChannels.containsKey(clientId)) {
            WebSocketChannel channel = activeChannels.get(clientId);
            WebSockets.sendText(JSON.toJSON(message), channel, null);
        }
    }

    public void addWebSocketChannel(String clientId, WebSocketChannel channel) {
        activeChannels.put(clientId, channel);

        WebSocketConnectMessage connectMessage = new WebSocketConnectMessage();
        connectMessage.clientId = clientId;
        connectMessageMessagePublisher.publish(connectMessage);
    }

    public void removeWebSocketChannel(String clientId) {
        activeChannels.remove(clientId);

        WebSocketDisconnectMessage disconnectMessage = new WebSocketDisconnectMessage();
        disconnectMessage.clientId = clientId;
        disconnectMessageMessagePublisher.publish(disconnectMessage);
    }

    public WebSocketConnectionCallback webSocketConnectionCallback() {
        return (exchange, channel) -> {
            Map<String, List<String>> headers = exchange.getRequestHeaders();
            Map<String, Cookie> cookies = Cookies.parseRequestCookies(200, false, headers.get(Headers.COOKIE.toString()));
            Cookie clientIdCookie = cookies.get("clientId");
            if (clientIdCookie == null || Strings.isNullOrEmpty(clientIdCookie.getValue())) {
                WebSocketMessage message = new WebSocketMessage();
                message.body = JSON.toJSON(Collections.singletonMap("errorMessage", "Missing client id"));
                WebSockets.sendText(JSON.toJSON(message), channel, null);
                try {
                    channel.close();
                } catch (IOException e) {
                    throw Errors.internalError(e);
                }
                return;
            }

            String clientId = clientIdCookie.getValue();
            addWebSocketChannel(clientId, channel);

            channel.getReceiveSetter().set(new AbstractReceiveListener() {
                @Override
                protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage bufferedTextMessage) {
                    WebSocketMessage message = new WebSocketMessage();
                    message.clientId = clientId;
                    message.body = bufferedTextMessage.getData();
                    onMessage(message);
                }
            });
            channel.getCloseSetter().set((c -> removeWebSocketChannel(clientId)));
            channel.resumeReceives();
        };
    }
}
