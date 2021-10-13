package app.message;

/**
 * @author chi
 */
public interface MessageConfig {
    <T> MessageConfig createTopic(Class<T> messageClass, TopicOptions options);

    <T> MessageConfig listen(Class<T> messageClass, MessageHandler<T> handler);
}
