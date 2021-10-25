/**
 * @author chi
 */
module app.rabbitmq {
    requires transitive app.module;
    requires transitive app.message;

    requires com.rabbitmq.client;
    requires org.apache.commons.pool2;

    exports app.rabbitmq;
}
