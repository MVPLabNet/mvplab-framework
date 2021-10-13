package app.message;

/**
 * @author chi
 */
public interface MessageVendor {
    <T> MessagePublisher<T> createTopic(Class<T> messageClass, TopicOptions topicOptions);

    <T> void listen(String messageGroup, Class<T> messageClass, MessageHandler<T> messageHandler);

    void start();

    void stop();
}
