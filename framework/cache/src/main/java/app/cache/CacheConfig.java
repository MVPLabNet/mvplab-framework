package app.cache;

import java.lang.reflect.Type;

/**
 * @author chi
 */
public interface CacheConfig {
    <T> Cache<T> create(Type type, CacheOptions options);

    void setVendor(CacheVendor vendor);
}
