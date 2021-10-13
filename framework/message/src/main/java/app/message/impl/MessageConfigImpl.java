package app.message.impl;

import app.AbstractModule;
import app.Binder;
import app.message.MessageConfig;
import app.message.MessageHandler;
import app.message.MessagePublisher;
import app.message.TopicOptions;
import app.util.type.Types;

/**
 * @author chi
 */
public class MessageConfigImpl implements MessageConfig {
    private final Binder binder;
    private final AbstractModule module;
    private final MessageManager messageManager;

    public MessageConfigImpl(Binder binder, AbstractModule module, MessageManager messageManager) {
        this.binder = binder;
        this.module = module;
        this.messageManager = messageManager;
    }

    @Override
    public <T> MessageConfig createTopic(Class<T> messageClass, TopicOptions topicOptions) {
        MessagePublisher<T> messagePublisher = messageManager.createTopic(messageClass, topicOptions);
        binder.bind(Types.generic(MessagePublisher.class, messageClass)).toInstance(messagePublisher);
        return this;
    }

    @Override
    public <T> MessageConfig listen(Class<T> messageClass, MessageHandler<T> handler) {
        messageManager.listen(module.getClass().getCanonicalName(), messageClass, handler);
        return this;
    }
}
