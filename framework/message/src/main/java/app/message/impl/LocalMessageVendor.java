package app.message.impl;

import app.message.MessagePublisher;
import app.message.MessageHandler;
import app.message.MessageVendor;
import app.message.TopicOptions;


/**
 * @author chi
 */
public class LocalMessageVendor implements MessageVendor {
    private final LocalMessageClient client = new LocalMessageClient();

    @Override
    public <T> MessagePublisher<T> createTopic(Class<T> messageClass, TopicOptions topicOptions) {
        return client.createTopic(topicName(messageClass), messageClass, topicOptions);
    }

    @Override
    public <T> void listen(String messageGroup, Class<T> messageClass, MessageHandler<T> messageHandler) {
        String topic = topicName(messageClass);
        client.listen(topic, messageGroup, messageClass, messageHandler);
    }

    @Override
    public void start() {
        client.start();
    }

    @Override
    public void stop() {
        client.stop();
    }

    private String topicName(Class<?> messageClass) {
        return messageClass.getCanonicalName();
    }
}
