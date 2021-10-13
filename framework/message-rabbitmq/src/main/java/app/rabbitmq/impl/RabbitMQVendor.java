package app.rabbitmq.impl;

import app.message.MessageHandler;
import app.message.MessagePublisher;
import app.message.MessageVendor;
import app.message.TopicOptions;

/**
 * @author chi
 */
public class RabbitMQVendor implements MessageVendor {
    private final RabbitMQClient client;

    public RabbitMQVendor(RabbitMQClient client) {
        this.client = client;
    }

    @Override
    public <T> MessagePublisher<T> createTopic(Class<T> messageClass, TopicOptions topicOptions) {
        String exchangeName = exchangeName(messageClass);
        client.declareExchange(exchangeName, topicOptions.durable);
        return new RabbitMQPublisher<>(exchangeName, client);
    }

    @Override
    public <T> void listen(String messageGroup, Class<T> messageClass, MessageHandler<T> messageHandler) {
        String exchangeName = exchangeName(messageClass);
        client.declareQueue(exchangeName, messageGroup);
        client.listen(messageGroup, messageClass, messageHandler);
    }

    @Override
    public void start() {
        client.start();
    }

    @Override
    public void stop() {
        client.stop();
    }

    private String exchangeName(Class<?> messageClass) {
        return messageClass.getCanonicalName();
    }
}
