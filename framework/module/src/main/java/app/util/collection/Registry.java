package app.util.collection;

import app.ApplicationException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author chi
 */
public class Registry<T, K> {
    private final Map<T, K> registry = Maps.newHashMap();
    private boolean overrideAllowed = false;
    private Comparator<K> comparator;

    public boolean put(T key, K value) {
        K original = registry.get(key);
        if (original == null) {
            registry.put(key, value);
            return true;
        }
        if (!overrideAllowed) {
            throw new ApplicationException("binding exists, key={}, value={}, original={}", key, value, original);
        }
        if (comparator != null) {
            if (comparator.compare(value, original) < 0) {
                registry.put(key, value);
                return true;
            }
            return false;
        } else {
            registry.put(key, value);
            return true;
        }
    }

    public Optional<K> get(T key) {
        return Optional.ofNullable(registry.get(key));
    }

    public List<T> keys() {
        return ImmutableList.copyOf(registry.keySet());
    }

    public List<K> values() {
        return ImmutableList.copyOf(registry.values());
    }

    public Registry<T, K> allowOverride() {
        overrideAllowed = true;
        return this;
    }

    public Registry<T, K> withComparator(Comparator<K> comparator) {
        this.comparator = comparator;
        return this;
    }
}
