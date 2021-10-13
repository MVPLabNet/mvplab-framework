package app.rabbitmq.impl;

import app.message.MessageHandler;
import app.util.exception.Errors;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author chi
 */
public class RabbitMQClient {
    private final Logger logger = LoggerFactory.getLogger(RabbitMQClient.class);
    private final GenericObjectPool<Channel> pool;
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final List<Thread> listenerThreads = Lists.newArrayList();
    private final ExecutorService workers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    public RabbitMQClient(RabbitMQOptions rabbitMQOptions) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername(rabbitMQOptions.username);
        connectionFactory.setPassword(rabbitMQOptions.password);
        connectionFactory.setVirtualHost(rabbitMQOptions.virtualHost);
        connectionFactory.setHost(rabbitMQOptions.host);
        connectionFactory.setPort(rabbitMQOptions.port);
        connectionFactory.setSharedExecutor(workers);
        connectionFactory.setAutomaticRecoveryEnabled(true);

        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMinIdle(8);
        genericObjectPoolConfig.setMaxTotal(18);
        genericObjectPoolConfig.setMinIdle(8);
        pool = new GenericObjectPool<>(new RabbitMQChannelFactory(createConnection(connectionFactory)), genericObjectPoolConfig);
    }

    public void declareExchange(String exchange, Boolean durable) {
        PoolableChannel channel = channel();
        try {
            channel.exchangeDeclare(exchange, "fanout", durable);
        } catch (IOException e) {
            channel.setValid(false);
            throw Errors.internalError(e);
        } finally {
            channel.close();
        }
    }

    public void declareQueue(String exchange, String queue) {
        PoolableChannel channel = channel();
        try {
            channel.queueDeclare(queue, true, false, false, ImmutableMap.of());
            channel.queueBind(queue, exchange, "");
        } catch (IOException e) {
            channel.setValid(false);
            throw Errors.internalError(e);
        } finally {
            channel.close();
        }
    }

    public <T> void listen(String queue, Class<T> messageClass, MessageHandler<T> messageHandler) {
        RabbitMQListener<T> listener = new RabbitMQListener<>(queue, messageClass, messageHandler, this, workers);
        listenerThreads.add(listener);
    }

    public void publish(String exchange, byte[] message) {
        PoolableChannel channel = channel();
        try {
            channel.basicPublish(exchange, "", null, message);
        } catch (IOException e) {
            channel.setValid(false);
            throw Errors.internalError(e);
        } finally {
            channel.close();
        }
    }

    private Connection createConnection(ConnectionFactory factory) {
        try {
            return factory.newConnection();
        } catch (Exception e) {
            throw Errors.internalError(e);
        }
    }

    public PoolableChannel channel() {
        try {
            return new PoolableChannel(pool.borrowObject(), pool);
        } catch (Exception e) {
            throw Errors.internalError(e);
        }
    }

    public void start() {
        logger.info("start RabbitMQ client");
        listenerThreads.forEach(Thread::start);
    }

    public void stop() {
        stopped.set(true);
        workers.shutdown();
        try {
            workers.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw Errors.internalError(e);
        }
    }

    public boolean isStopped() {
        return stopped.get();
    }

    private static class RabbitMQChannelFactory implements PooledObjectFactory<Channel> {
        private final Connection connection;

        RabbitMQChannelFactory(Connection connection) {
            this.connection = connection;
        }

        @Override
        public PooledObject<Channel> makeObject() throws Exception {
            return new DefaultPooledObject<>(connection.createChannel());
        }

        @Override
        public void destroyObject(PooledObject<Channel> p) throws Exception {
            Channel channel = p.getObject();
            channel.close();
        }

        @Override
        public boolean validateObject(PooledObject<Channel> p) {
            return true;
        }

        @Override
        public void activateObject(PooledObject<Channel> p) throws Exception {
        }

        @Override
        public void passivateObject(PooledObject<Channel> p) throws Exception {
        }
    }
}
