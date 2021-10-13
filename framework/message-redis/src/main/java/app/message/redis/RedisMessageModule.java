package app.message.redis;

import app.message.MessageModule;
import app.message.MessageVendor;
import app.message.redis.impl.RedisMessageVendor;


/**
 * @author chi
 */
public class RedisMessageModule extends MessageModule {
    @Override
    protected MessageVendor vendor() {
        return new RedisMessageVendor(options("redis-message", RedisMessageOptions.class));
    }
}