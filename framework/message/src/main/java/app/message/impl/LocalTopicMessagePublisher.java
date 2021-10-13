package app.message.impl;

import app.message.MessagePublisher;

/**
 * @author chi
 */
class LocalTopicMessagePublisher<T> implements MessagePublisher<T> {
    private final String topic;
    private final LocalMessageClient client;

    LocalTopicMessagePublisher(String topic, LocalMessageClient client) {
        this.topic = topic;
        this.client = client;
    }

    @Override
    public void publish(T message) {
        client.publish(topic, message);
    }

    @Override
    public void publish(String key, T message) {
        client.publish(topic, message);
    }
}
