/**
 * @author chi
 */
module app.redis {
    requires transitive app.module;
    requires transitive app.cache;
    requires jedis;
    exports app.cache.redis;
}