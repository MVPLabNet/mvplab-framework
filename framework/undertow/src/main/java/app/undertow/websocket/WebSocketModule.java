package app.undertow.websocket;

import app.AbstractModule;
import app.message.MessageConfig;
import app.message.MessageModule;
import app.message.TopicOptions;

/**
 * @author chi
 */
public class WebSocketModule extends AbstractModule {
    public WebSocketModule() {
        super(MessageModule.class);
    }

    @Override
    protected void configure() {
        MessageConfig messageConfig = module(MessageModule.class);
        TopicOptions webSocketMessageOptions = new TopicOptions();
        webSocketMessageOptions.broadcast = true;
        messageConfig.createTopic(WebSocketMessage.class, webSocketMessageOptions);
        messageConfig.listen(WebSocketMessage.class, requestInjection(new WebSocketMessageHandler()));

        messageConfig.createTopic(WebSocketConnectMessage.class, new TopicOptions());
        messageConfig.createTopic(WebSocketDisconnectMessage.class, new TopicOptions());


        bind(WebSocketService.class);
        bindController(UndertowWebSocketMessageWebController.class);
    }
}
