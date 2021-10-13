/**
 * @author chi
 */
module app.message.redis {
    requires transitive app.module;
    requires transitive app.message;

    requires jedis;
    requires commons.pool2;

    exports app.message.redis;
}