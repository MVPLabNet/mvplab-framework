/**
 * @author chi
 */
module app.rabbitmq {
    requires transitive app.module;
    requires transitive app.message;

    requires com.rabbitmq.client;
    requires commons.pool2;

    exports app.rabbitmq;
}