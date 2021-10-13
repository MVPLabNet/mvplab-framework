package app.template;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author chi
 */
public class Attributes implements Map<String, Object> {
    private final Map<String, Object> props;

    public Attributes(Map<String, Object> values) {
        this.props = values;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T) props.get(name);
    }

    @Override
    public Object get(Object key) {
        return props.get(key);
    }

    @Override
    public int size() {
        return props.size();
    }

    @Override
    public boolean isEmpty() {
        return props.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return props.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return props.containsValue(value);
    }

    @Override
    public Object put(String key, Object value) {
        return props.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return props.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        props.putAll(m);
    }

    @Override
    public void clear() {
        props.clear();
    }

    @Override
    public Set<String> keySet() {
        return props.keySet();
    }

    @Override
    public Collection<Object> values() {
        return props.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return props.entrySet();
    }
}
