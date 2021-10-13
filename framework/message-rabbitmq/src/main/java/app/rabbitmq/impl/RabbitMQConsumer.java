package app.rabbitmq.impl;

import com.google.common.base.Charsets;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import app.message.MessageHandler;
import app.message.TopicOptions;
import app.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.LockSupport;

/**
 * @author chi
 */
public class RabbitMQConsumer<T> implements Consumer, AutoCloseable {
    private final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);
    private final String queue;
    private final Channel channel;
    private final TopicOptions options;
    private final Class<T> messageClass;
    private final Thread listenerThread;
    private final MessageHandler<T> messageHandler;
    private final ExecutorService workers;

    public RabbitMQConsumer(String queue, Channel channel, Class<T> messageClass, TopicOptions options, MessageHandler<T> messageHandler, ExecutorService workers) {
        this.queue = queue;
        this.channel = channel;
        this.messageClass = messageClass;
        this.options = options;
        this.messageHandler = messageHandler;
        this.workers = workers;
        listenerThread = Thread.currentThread();
    }

    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException shutdown) {
        LockSupport.unpark(listenerThread);
    }

    @Override
    public void handleRecoverOk(String consumerTag) {

    }

    @Override
    public void handleConsumeOk(String consumerTag) {

    }

    @Override
    public void handleCancelOk(String consumerTag) {

    }

    @Override
    public void handleCancel(String consumerTag) throws IOException {
        LockSupport.unpark(listenerThread);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
        long deliveryTag = envelope.getDeliveryTag();
        workers.submit(() -> {
            try {
                messageHandler.handle(JSON.fromJSON(body, messageClass));
            } catch (Throwable e) {
                logger.error("failed to process message, type={}, message={}", messageClass.getCanonicalName(), new String(body, Charsets.UTF_8), e);
            } finally {
                acknowledge(deliveryTag);
            }
        });
    }

    private void acknowledge(long deliveryTag) {
        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    public void start() {
        try {
            channel.basicQos(options.prefetchSize);
            channel.basicConsume(queue, false, this);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        LockSupport.park();
    }

    @Override
    public void close() {
        try {
            channel.close();
        } catch (ShutdownSignalException e) {
            logger.debug("connection is closed", e);
        } catch (IOException | TimeoutException e) {
            logger.warn("failed to close channel", e);
        }
    }

}
