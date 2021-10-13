package app.cache.local.impl;

import app.cache.Cache;
import app.cache.CacheOptions;
import app.cache.CacheVendor;
import app.Binder;
import app.cache.CacheConfig;
import app.util.type.Types;

import java.lang.reflect.Type;

/**
 * @author chi
 */
public class CacheConfigImpl implements CacheConfig {
    private final Binder binder;
    private final CacheManager cacheManager;

    public CacheConfigImpl(Binder binder, CacheManager cacheManager) {
        this.binder = binder;
        this.cacheManager = cacheManager;
    }

    @Override
    public <T> Cache<T> create(Type type, CacheOptions options) {
        Cache<T> cache = cacheManager.create(type, options);
        binder.bind(Types.generic(Cache.class, type)).toInstance(cache);
        return cache;
    }

    @Override
    public void setVendor(CacheVendor vendor) {
        cacheManager.setVendor(vendor);
    }
}
