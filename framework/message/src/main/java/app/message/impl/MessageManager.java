package app.message.impl;

import app.message.MessageHandler;
import app.message.MessagePublisher;
import app.message.MessageVendor;
import app.message.TopicOptions;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author chi
 */
public class MessageManager {
    private final Map<Class<?>, MessagePublisher<?>> topics = Maps.newConcurrentMap();
    private final MessageVendor messageVendor;

    public MessageManager(MessageVendor messageVendor) {
        this.messageVendor = messageVendor;
    }

    @SuppressWarnings("unchecked")
    public <T> MessagePublisher<T> createTopic(Class<T> messageClass, TopicOptions topicOptions) {
        return (MessagePublisher<T>) topics.computeIfAbsent(messageClass, message -> messageVendor.createTopic((Class<T>) message, topicOptions));
    }

    public <T> void listen(String messageGroup, Class<T> messageClass, MessageHandler<T> handler) {
        messageVendor.listen(messageGroup, messageClass, handler);
    }

    public void start() {
        messageVendor.start();
    }

    public void stop() {
        messageVendor.stop();
    }
}
