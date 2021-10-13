package app.message.redis.impl;

import app.message.MessagePublisher;
import app.message.TopicOptions;
import app.message.redis.RedisMessageOptions;
import app.util.JSON;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chi
 */
@Disabled
class RedisMessageVendorTest {
    private final Logger logger = LoggerFactory.getLogger(RedisMessageVendorTest.class);


    @Test
    void publish() throws InterruptedException {
        RedisMessageOptions options = new RedisMessageOptions();
        options.host = "localhost";
        options.port = 6379;

        RedisMessageVendor vendor = new RedisMessageVendor(options);
        MessagePublisher<TestMessage> publisher = vendor.createTopic(TestMessage.class, new TopicOptions());
        vendor.listen("test", TestMessage.class, message -> logger.info("get message, {}", JSON.toJSON(message)));
        vendor.listen("test", TestMessage.class, message -> logger.info("get message, {}", JSON.toJSON(message)));
        vendor.start();

        TestMessage message = new TestMessage();
        message.value = "test";
        publisher.publish(message);


        Thread.currentThread().join();
        vendor.stop();
    }
}