package app.util.i18n;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author chi
 */
public class MapMessageBundle implements MessageBundle {
    private final Map<String, String> messages;

    public MapMessageBundle(Map<String, String> messages) {
        this.messages = messages;
    }

    @Override
    public Optional<String> get(String key, String language) {
        return Optional.ofNullable(messages.get(key));
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(messages.get(key));
    }

    @Override
    public List<String> keys() {
        return ImmutableList.copyOf(messages.keySet());
    }
}
