package app.message.redis.impl;

import app.message.MessageHandler;
import app.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.StreamEntry;
import redis.clients.jedis.StreamEntryID;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author chi
 */
public class RedisMessageListener<T> {
    private final Logger logger = LoggerFactory.getLogger(RedisMessageListener.class);
    private final String messageGroup;
    private final Class<T> messageClass;
    private final MessageHandler<T> messageHandler;
    private final JedisPool pool;
    private final ExecutorService workers;
    private final Thread thread;
    private volatile boolean running = true;

    public RedisMessageListener(String messageGroup, Class<T> messageClass, MessageHandler<T> messageHandler, JedisPool pool, ExecutorService workers) {
        this.messageGroup = messageGroup;
        this.messageClass = messageClass;
        this.messageHandler = messageHandler;
        this.pool = pool;
        this.workers = workers;
        this.thread = new Thread(this::run, "redis-message-" + messageGroup);
    }

    public String messageGroup() {
        return messageGroup;
    }

    public void start() {
        this.running = true;
        thread.start();
    }

    public void stop() {
        this.running = false;
        thread.interrupt();
    }

    @SuppressWarnings("unchecked")
    public void run() {
        while (running) {
            try (Jedis jedis = pool.getResource()) {
                Map.Entry<String, StreamEntryID> query = new AbstractMap.SimpleImmutableEntry<>(streamName(messageClass), StreamEntryID.UNRECEIVED_ENTRY);
                List<Map.Entry<String, List<StreamEntry>>> messages = jedis.xreadGroup(messageGroup, streamName(messageClass), 10, 10000, true, query);
                for (Map.Entry<String, List<StreamEntry>> item : messages) {
                    Map<String, String> value = item.getValue().get(0).getFields();
                    workers.execute(doHandle(value));
                }
            }
        }
    }

    private Runnable doHandle(Map<String, String> value) {
        return () -> {
            try {
                T message = JSON.fromJSON(value.get("value"), messageClass);
                messageHandler.handle(message);
            } catch (Throwable e) {
                logger.error("failed to process message, type={}", messageClass.getCanonicalName(), e);
            }
        };
    }

    private String streamName(Class<?> messageClass) {
        return messageClass.getCanonicalName();
    }
}
