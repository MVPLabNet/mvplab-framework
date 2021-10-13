package app.admin.impl.service;

import app.admin.ConsoleBundle;
import com.google.common.collect.Lists;
import app.util.i18n.CompositeMessageBundle;
import app.util.i18n.MessageBundle;

import java.util.List;
import java.util.Optional;

/**
 * @author chi
 */
public class ConsoleMessageBundle implements MessageBundle {
    private final ConsoleBundle consoleBundle;
    private final CompositeMessageBundle compositeMessageBundle;

    public ConsoleMessageBundle(ConsoleBundle consoleBundle, CompositeMessageBundle compositeMessageBundle) {
        this.consoleBundle = consoleBundle;
        this.compositeMessageBundle = compositeMessageBundle;
    }

    @Override
    public Optional<String> get(String key) {
        return compositeMessageBundle.get(key);
    }

    @Override
    public Optional<String> get(String key, String language) {
        return compositeMessageBundle.get(key, language);
    }

    @Override
    public List<String> keys() {
        List<String> keys = Lists.newArrayList();
        for (String s : consoleBundle.messages) {
            Optional<MessageBundle> messageBundle = this.compositeMessageBundle.messageBundle(s);
            messageBundle.ifPresent(messageBundle1 -> keys.addAll(messageBundle1.keys()));
        }
        return keys;
    }
}
