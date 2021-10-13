package app.message;

import app.AbstractModule;
import app.Binder;
import app.Configurable;
import app.message.impl.LocalMessageVendor;
import app.message.impl.MessageConfigImpl;
import app.message.impl.MessageManager;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author chi
 */
public class MessageModule extends AbstractModule implements Configurable<MessageConfig> {
    private MessageManager messageManager;

    public MessageModule() {
        super();
    }

    @SafeVarargs
    public MessageModule(Class<? extends AbstractModule>... dependencies) {
        super(dependencies);
    }

    @Override
    protected void configure() {
        messageManager = new MessageManager(vendor());
        bind(MessageManager.class).toInstance(messageManager);

        onReady(messageManager::start);
        onShutdown(messageManager::stop);
    }

    protected MessageVendor vendor() {
        return new LocalMessageVendor();
    }

    @Override
    public List<String> declareRoles() {
        return ImmutableList.of();
    }

    @Override
    public MessageConfig configurator(AbstractModule module, Binder binder) {
        return new MessageConfigImpl(binder, module, messageManager);
    }
}