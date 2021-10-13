package app.cache.local.impl;

import app.cache.Cache;
import app.cache.CacheOptions;
import app.cache.CacheVendor;
import app.util.exception.Errors;
import app.util.type.Types;
import com.google.common.collect.Maps;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author chi
 */
public class CacheManager {
    private final Map<String, Cache<?>> caches = Maps.newHashMap();
    private CacheVendor vendor;

    @SuppressWarnings("unchecked")
    public <T> Cache<T> cache(Class<T> cacheClass) {
        String cacheName = cacheName(cacheClass);
        Cache<T> cache = (Cache<T>) caches.get(cacheName);
        if (cache == null) {
            throw Errors.internalError("missing cache, type={}", cacheClass);
        }
        return cache;
    }

    public void setVendor(CacheVendor vendor) {
        this.vendor = vendor;
    }

    public <T> Cache<T> create(Type type, CacheOptions options) {
        String cacheName = cacheName(type);
        Cache<T> cache = vendor.create(type, cacheName, options);
        caches.put(cacheName, cache);
        return cache;
    }

    private String cacheName(Type type) {
        return Types.className(type);
    }
}
