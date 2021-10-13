package app.rabbitmq.impl;

import app.message.MessageHandler;
import app.util.JSON;
import app.util.exception.Errors;
import com.rabbitmq.client.GetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * @author chi
 */
public class RabbitMQListener<T> extends Thread {
    private final Logger logger = LoggerFactory.getLogger(RabbitMQListener.class);
    private final String queue;
    private final Class<T> messageClass;
    private final MessageHandler<T> messageHandler;
    private final RabbitMQClient client;
    private final ExecutorService workers;

    public RabbitMQListener(String queue, Class<T> messageClass, MessageHandler<T> messageHandler, RabbitMQClient client, ExecutorService workers) {
        this.queue = queue;
        this.messageClass = messageClass;
        this.messageHandler = messageHandler;
        this.client = client;
        this.workers = workers;
    }

    @Override
    public void run() {
        while (!client.isStopped()) {
            PoolableChannel channel = client.channel();
            try {
                GetResponse response = channel.basicGet(queue, false);
                if (response != null) {
                    workers.submit(doHandle(response, channel));
                } else {
                    channel.close();
                    sleepSeconds(1);
                }
            } catch (Throwable e) {
                logger.error("failed to pull message", e);
                channel.setValid(false);
                channel.close();
                sleepSeconds(10);
            }
        }
    }

    private void sleepSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            throw Errors.internalError(e);
        }
    }

    private Runnable doHandle(GetResponse response, PoolableChannel channel) {
        return () -> {
            try {
                T message = JSON.fromJSON(response.getBody(), messageClass);
                messageHandler.handle(message);
                channel.basicAck(response.getEnvelope().getDeliveryTag(), false);
            } catch (Throwable e) {
                channel.setValid(false);
                logger.error("failed to process message, type={}", messageClass.getCanonicalName(), e);
            }
        };
    }
}
