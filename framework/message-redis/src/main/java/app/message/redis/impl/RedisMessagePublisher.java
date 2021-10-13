package app.message.redis.impl;

import app.message.MessagePublisher;
import app.util.JSON;
import com.google.common.collect.Maps;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/**
 * @author chi
 */
public class RedisMessagePublisher<T> implements MessagePublisher<T> {
    private final String streamName;
    private final JedisPool pool;

    public RedisMessagePublisher(String streamName, JedisPool pool) {
        this.streamName = streamName;
        this.pool = pool;
    }

    @Override
    public void publish(T message) {
        publish(null, message);
    }

    @Override
    public void publish(String key, T message) {
        try (Jedis jedis = pool.getResource()) {
            Map<String, String> value = Maps.newHashMap();
            value.put("value", JSON.toJSON(message));
            jedis.xadd(streamName, null, value);
        }
    }
}
