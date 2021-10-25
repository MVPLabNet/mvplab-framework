/**
 * @author chi
 */
module app.message.redis {
    requires transitive app.module;
    requires transitive app.message;

    requires redis.clients.jedis;
    requires org.apache.commons.pool2;

    exports app.message.redis;
}
