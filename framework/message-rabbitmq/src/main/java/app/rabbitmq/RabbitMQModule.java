package app.rabbitmq;

import app.message.MessageModule;
import app.message.MessageVendor;
import app.rabbitmq.impl.RabbitMQClient;
import app.rabbitmq.impl.RabbitMQOptions;
import app.rabbitmq.impl.RabbitMQVendor;


/**
 * @author chi
 */
public class RabbitMQModule extends MessageModule {
    @Override
    protected MessageVendor vendor() {
        return new RabbitMQVendor(new RabbitMQClient(options("rabbitmq", RabbitMQOptions.class)));
    }
}