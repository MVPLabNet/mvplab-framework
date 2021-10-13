package app.message.impl;

import app.message.MessageHandler;
import app.util.exception.Errors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

/**
 * @author chi
 */
public class LocalMessageListener<T> extends Thread {
    private final Logger logger = LoggerFactory.getLogger(LocalMessageListener.class);

    private final Class<T> messageClass;
    private final BlockingQueue<T> queue;
    private final LocalMessageClient client;
    private final MessageHandler<T> messageHandler;
    private final ExecutorService workers;

    public LocalMessageListener(Class<T> messageClass, BlockingQueue<T> queue, LocalMessageClient client, MessageHandler<T> messageHandler, ExecutorService workers) {
        this.messageClass = messageClass;
        this.queue = queue;
        this.client = client;
        this.messageHandler = messageHandler;
        this.workers = workers;
    }

    @Override
    public void run() {
        while (client.isRunning()) {
            T message = queue.poll();
            if (message != null) {
                workers.submit(() -> {
                    try {
                        messageHandler.handle(message);
                    } catch (Throwable e) {
                        logger.error("failed to process message, messageClass={}", messageClass.getCanonicalName(), e);
                    }
                });
            } else {
                sleepSeconds(1);
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
}
