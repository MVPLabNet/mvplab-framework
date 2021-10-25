/**
 * @author chi
 */
module app.redis {
    requires transitive app.module;
    requires transitive app.cache;
    requires redis.clients.jedis;
    exports app.cache.redis;
}
