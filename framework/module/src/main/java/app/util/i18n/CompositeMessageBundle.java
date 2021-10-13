package app.util.i18n;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import app.util.collection.Registry;

import java.util.List;
import java.util.Optional;

/**
 * @author chi
 */
public class CompositeMessageBundle implements MessageBundle {
    private final Registry<String, MessageBundle> registry = new Registry<String, MessageBundle>().allowOverride();
    private final Cache<String, String> cache = CacheBuilder.newBuilder().build();

    public CompositeMessageBundle bind(String path, MessageBundle bundle) {
        registry.put(path, bundle);
        return this;
    }

    public Optional<MessageBundle> messageBundle(String path) {
        return registry.get(path);
    }

    @Override
    public Optional<String> get(String key) {
        String cacheKey = key(key, null);
        String value = cache.getIfPresent(cacheKey);
        if (value != null) {
            return Optional.of(value);
        }
        for (MessageBundle bundle : registry.values()) {
            Optional<String> message = bundle.get(key);
            if (message.isPresent()) {
                cache.put(cacheKey, message.get());
                return message;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> get(String key, String language) {
        String cacheKey = key(key, language);
        String value = cache.getIfPresent(cacheKey);
        if (value != null) {
            return Optional.of(value);
        }
        for (MessageBundle bundle : registry.values()) {
            Optional<String> message = bundle.get(key, language);
            if (message.isPresent()) {
                cache.put(cacheKey, message.get());
                return message;
            }
        }
        return Optional.empty();
    }

    private String key(String key, String language) {
        return language == null ? key : key + '-' + language;
    }

    @Override
    public List<String> keys() {
        List<String> keys = Lists.newArrayList();
        registry.values().forEach(messageBundle -> keys.addAll(messageBundle.keys()));
        return keys;
    }
}
