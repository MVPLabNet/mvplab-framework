package app.message.impl;

import app.message.MessageHandler;
import app.message.MessagePublisher;
import app.message.TopicOptions;
import app.util.exception.Errors;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author chi
 */
public class LocalMessageClient {
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final Map<String, Map<String, BlockingQueue<?>>> topics = Maps.newConcurrentMap();
    private final List<Thread> listenerThreads = Lists.newArrayList();
    private final ExecutorService workers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);

    public <T> void publish(String topic, T message) {
        for (BlockingQueue<T> queue : this.<T>queues(topic)) {
            queue.add(message);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Collection<BlockingQueue<T>> queues(String topic) {
        Map<String, BlockingQueue<?>> queues = topics.get(topic);
        if (queues == null) {
            throw Errors.internalError("missing topic, name={}", topic);
        }
        return queues.values().stream().map(queue -> (BlockingQueue<T>) queue).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <T> void listen(String topic, String messageGroup, Class<T> messageClass, MessageHandler<T> messageHandler) {
        Map<String, BlockingQueue<?>> queues = this.topics.get(topic);
        if (queues == null) {
            return;
        }
        BlockingQueue<T> blockingQueue = (BlockingQueue<T>) queues.get(messageGroup);
        if (blockingQueue == null) {
            blockingQueue = new LinkedBlockingQueue<>(100000);
            queues.put(messageGroup, blockingQueue);
        }
        LocalMessageListener<T> listener = new LocalMessageListener<>(messageClass, blockingQueue, this, messageHandler, workers);
        listenerThreads.add(listener);
    }

    public <T> MessagePublisher<T> createTopic(String topic, Class<T> messageClass, TopicOptions options) {
        LocalTopicMessagePublisher<T> publisher = new LocalTopicMessagePublisher<>(topic, this);
        topics.put(topic, Maps.newHashMap());
        return publisher;
    }


    public boolean isRunning() {
        return running.get();
    }

    public void start() {
        listenerThreads.forEach(Thread::start);
    }

    public void stop() {
        running.set(false);
        workers.shutdown();
        try {
            workers.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw Errors.internalError(e);
        }
    }
}
