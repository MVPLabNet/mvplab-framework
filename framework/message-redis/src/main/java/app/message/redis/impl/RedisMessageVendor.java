package app.message.redis.impl;

import app.message.MessageHandler;
import app.message.MessagePublisher;
import app.message.MessageVendor;
import app.message.TopicOptions;
import app.message.redis.RedisMessageOptions;
import com.google.common.collect.Lists;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.StreamGroupInfo;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chi
 */
public class RedisMessageVendor implements MessageVendor {
    private final JedisPool jedisPool;
    private final List<RedisMessageListener<?>> listeners = Lists.newArrayList();
    private final ExecutorService workers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public RedisMessageVendor(RedisMessageOptions options) {
        jedisPool = new JedisPool(options.host, options.port);
    }

    @Override
    public <T> MessagePublisher<T> createTopic(Class<T> messageClass, TopicOptions topicOptions) {
        String streamName = streamName(messageClass);
        return new RedisMessagePublisher<>(streamName, jedisPool);
    }

    @Override
    public <T> void listen(String messageGroup, Class<T> messageClass, MessageHandler<T> messageHandler) {
        String streamName = streamName(messageClass);
        try (Jedis jedis = jedisPool.getResource()) {
            if (!isMessageGroupExists(streamName, messageGroup)) {
                jedis.xgroupCreate(streamName, messageGroup, null, true);
            }
        }
        RedisMessageListener<T> messageListener = new RedisMessageListener<>(messageGroup, messageClass, messageHandler, jedisPool, workers);
        listeners.add(messageListener);
    }

    private boolean isMessageGroupExists(String streamName, String messageGroup) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<StreamGroupInfo> streamGroupInfos = jedis.xinfoGroup(streamName);
            for (StreamGroupInfo streamGroupInfo : streamGroupInfos) {
                if (streamGroupInfo.getName().equalsIgnoreCase(messageGroup)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void start() {
        for (RedisMessageListener<?> listener : listeners) {
            listener.start();
        }
    }

    @Override
    public void stop() {
        for (RedisMessageListener<?> listener : listeners) {
            listener.stop();
        }
        jedisPool.close();
    }

    private String streamName(Class<?> messageClass) {
        return messageClass.getCanonicalName();
    }
}
