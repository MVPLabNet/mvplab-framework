package app.cache.redis.service;

import app.cache.Cache;
import app.cache.CacheOptions;
import app.cache.CacheVendor;
import app.cache.redis.RedisOptions;
import app.util.JSON;
import app.util.type.Types;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author chi
 */
public class RedisCacheVendor implements CacheVendor {
    private final JedisPool jedisPool;

    public RedisCacheVendor(RedisOptions options) {
        jedisPool = new JedisPool(options.host, options.port);
    }

    @Override
    public <T> Cache<T> create(Type cacheClass, String cacheName, CacheOptions options) {
        return new RedisCache<>(cacheClass, cacheName, options, jedisPool);
    }

    static class RedisCache<T> implements Cache<T> {
        private final Type cacheClass;
        private final String cacheName;
        private final CacheOptions options;
        private final JedisPool jedisPool;

        RedisCache(Type cacheClass, String cacheName, CacheOptions options, JedisPool jedisPool) {
            this.cacheClass = cacheClass;
            this.cacheName = cacheName;
            this.options = options;
            this.jedisPool = jedisPool;
        }

        @Override
        public Optional<T> get(String key) {
            try (BinaryJedis jedis = jedisPool.getResource()) {
                byte[] json = jedis.get(redisKey(key));
                if (json == null) {
                    return Optional.empty();
                } else {
                    RedisCacheItem<T> item = JSON.fromJSON(json, Types.generic(RedisCacheItem.class, cacheClass));
                    if (isValid(item)) {
                        return Optional.ofNullable(item.value);
                    }
                    return Optional.empty();
                }
            }
        }

        @Override
        public Map<String, T> batchGet(List<String> keys) {
            try (BinaryJedis jedis = jedisPool.getResource()) {
                byte[][] cacheKeys = new byte[keys.size()][];
                for (int i = 0; i < keys.size(); i++) {
                    cacheKeys[i] = redisKey(keys.get(i));
                }
                List<byte[]> jsonValues = jedis.mget(cacheKeys);
                Map<String, T> values = Maps.newHashMap();
                for (int i = 0; i < keys.size(); i++) {
                    byte[] json = jsonValues.get(i);
                    if (json != null) {
                        RedisCacheItem<T> item = JSON.fromJSON(json, Types.generic(RedisCacheItem.class, cacheClass));
                        if (isValid(item)) {
                            values.put(keys.get(i), item.value);
                        }
                    }
                }
                return values;
            }
        }

        @Override
        public void put(String key, T value) {
            try (BinaryJedis jedis = jedisPool.getResource()) {
                jedis.set(redisKey(key), JSON.toJSONBytes(redisValue(value)));
            }
        }

        @Override
        public void batchPut(Map<String, T> values) {
            try (BinaryJedis jedis = jedisPool.getResource()) {
                byte[][] params = new byte[values.size() * 2][];
                int i = 0;
                for (Map.Entry<String, T> entry : values.entrySet()) {
                    params[i * 2] = redisKey(entry.getKey());
                    params[i * 2 + 1] = entry.getValue() == null ? null : JSON.toJSONBytes(redisValue(entry.getValue()));
                    i++;
                }
                jedis.mset(params);
            }
        }

        @Override
        public void delete(String key) {
            try (BinaryJedis jedis = jedisPool.getResource()) {
                jedis.del(redisKey(key));
            }
        }

        @Override
        public void batchDelete(List<String> keys) {
            try (BinaryJedis jedis = jedisPool.getResource()) {
                byte[][] cacheKeys = new byte[keys.size()][];
                for (int i = 0; i < keys.size(); i++) {
                    cacheKeys[i] = redisKey(keys.get(i));
                }
                jedis.del(cacheKeys);
            }
        }

        private byte[] redisKey(String key) {
            String cacheKey = cacheName + key;
            return cacheKey.getBytes(Charsets.UTF_8);
        }

        private boolean isValid(RedisCacheItem<T> value) {
            return (System.currentTimeMillis() - value.timestamp) < options.expireTime.toMillis();
        }

        private RedisCacheItem<T> redisValue(T value) {
            RedisCacheItem<T> redisCacheItem = new RedisCacheItem<>();
            redisCacheItem.timestamp = System.currentTimeMillis();
            redisCacheItem.value = value;
            return redisCacheItem;
        }
    }

    static class RedisCacheItem<T> {
        public T value;
        public long timestamp;
    }
}
