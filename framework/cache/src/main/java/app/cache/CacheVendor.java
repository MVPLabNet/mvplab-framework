package app.cache;

import java.lang.reflect.Type;

/**
 * @author chi
 */
public interface CacheVendor {
    <T> Cache<T> create(Type type, String cacheName, CacheOptions options);
}
