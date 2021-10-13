package app.message;

/**
 * @author chi
 */
public interface MessagePublisher<T> {
    void publish(T message);

    void publish(String key, T message);
}