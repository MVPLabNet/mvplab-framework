package app.web.impl;

import app.web.WebOptions;
import com.google.common.collect.Maps;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.Duration;
import java.util.Map;

/**
 * @author chi
 */
public class RedisSessionRepository implements SessionRepository {
    private final JedisPool jedisPool;
    private final Duration expire;

    public RedisSessionRepository(WebOptions.SessionOptions options) {
        jedisPool = new JedisPool(options.redis.host, options.redis.port);
        expire = options.expire;
    }

    @Override
    public Map<String, String> load(String sessionId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String redisKey = redisKey(sessionId);
            Map<String, String> data = jedis.hgetAll(redisKey);
            if (data == null) {
                data = Maps.newHashMap();
            } else {
                jedis.expire(redisKey, (int) expire.getSeconds());
            }
            return data;
        }
    }

    @Override
    public void update(String sessionId, Map<String, String> data) {
        try (Jedis jedis = jedisPool.getResource()) {
            String redisKey = redisKey(sessionId);
            jedis.hmset(redisKey, data);
            jedis.expire(redisKey, (int) expire.getSeconds());
        }
    }

    @Override
    public void remove(String sessionId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String redisKey = redisKey(sessionId);
            jedis.del(redisKey);
        }
    }

    private String redisKey(String sessionId) {
        return "session/" + sessionId;
    }
}
