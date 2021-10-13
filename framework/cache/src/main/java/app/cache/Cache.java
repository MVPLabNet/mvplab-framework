package app.cache;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author chi
 */
public interface Cache<T> {
    Optional<T> get(String key);

    Map<String, T> batchGet(List<String> keys);

    void put(String key, T value);

    void batchPut(Map<String, T> values);

    void delete(String key);

    void batchDelete(List<String> keys);
}
