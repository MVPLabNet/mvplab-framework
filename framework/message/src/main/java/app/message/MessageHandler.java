package app.message;

/**
 * @author chi
 */
public interface MessageHandler<T> {
    void handle(T message) throws Throwable;
}
