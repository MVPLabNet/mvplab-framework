package app.rabbitmq.impl;

import app.message.MessagePublisher;
import app.util.JSON;
import app.util.exception.Errors;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author chi
 */
public class RabbitMQPublisher<T> implements MessagePublisher<T> {
    private final String exchangeName;
    private final RabbitMQClient rabbitMQClient;

    public RabbitMQPublisher(String exchangeName, RabbitMQClient rabbitMQClient) {
        this.exchangeName = exchangeName;
        this.rabbitMQClient = rabbitMQClient;
    }

    @Override
    public void publish(T message) {
        publish(null, message);
    }

    @Override
    public void publish(String key, T message) {
        try (Channel channel = rabbitMQClient.channel()) {
            channel.basicPublish(exchangeName, "", null, JSON.toJSONBytes(message));
        } catch (IOException | TimeoutException e) {
            throw Errors.internalError(e);
        }
    }
}
