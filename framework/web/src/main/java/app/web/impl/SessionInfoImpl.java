package app.web.impl;


import app.web.SessionInfo;

import java.util.Map;
import java.util.Optional;

/**
 * @author chi
 */
public class SessionInfoImpl implements SessionInfo {
    private final String sessionId;
    private final SessionRepository repository;

    public Map<String, String> data;
    public boolean changed = false;
    public boolean invalidated = false;

    public SessionInfoImpl(String sessionId, SessionRepository repository) {
        this.sessionId = sessionId;
        this.repository = repository;
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(data().get(key));
    }

    @Override
    public void put(String key, String value) {
        data().put(key, value);
        changed = true;
    }

    @Override
    public void delete(String key) {
        data().remove(key);
        changed = true;
    }

    @Override
    public void invalidate() {
        invalidated = true;
        data().clear();
    }

    private Map<String, String> data() {
        if (data == null) {
            data = repository.load(sessionId);
        }
        return data;
    }
}
