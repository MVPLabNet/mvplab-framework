package app.web.impl;

import app.web.WebOptions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author chi
 */
public class LocalSessionRepository implements SessionRepository {
    private final Cache<String, Map<String, String>> cache;

    public LocalSessionRepository(WebOptions.SessionOptions options) {
        this.cache = CacheBuilder.newBuilder()
            .expireAfterAccess(options.expire.toMillis(), TimeUnit.MILLISECONDS)
            .build();
    }

    @Override
    public Map<String, String> load(String sessionId) {
        Map<String, String> data = cache.getIfPresent(sessionId);
        if (data == null) {
            data = Maps.newHashMap();
            cache.put(sessionId, data);
        }
        return data;
    }

    @Override
    public void update(String sessionId, Map<String, String> data) {
        cache.put(sessionId, data);
    }

    @Override
    public void remove(String sessionId) {
        cache.invalidate(sessionId);
    }
}
