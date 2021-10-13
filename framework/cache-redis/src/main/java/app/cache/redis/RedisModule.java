package app.cache.redis;

import app.AbstractModule;
import app.cache.CacheModule;
import app.cache.redis.service.NoneCacheVendor;
import app.cache.redis.service.RedisCacheVendor;

/**
 * @author chi
 */
public class RedisModule extends AbstractModule {
    public RedisModule() {
        super(CacheModule.class);
    }

    @Override
    public void configure() {
        RedisOptions options = options("redis-cache", RedisOptions.class);
        if (Boolean.TRUE.equals(options.enabled)) {
            module(CacheModule.class).setVendor(new RedisCacheVendor(options));
        } else {
            module(CacheModule.class).setVendor(new NoneCacheVendor());
        }
    }
}
