package app.web.impl;

import java.util.Map;

/**
 * @author chi
 */
public interface SessionRepository {
    Map<String, String> load(String sessionId);

    void update(String sessionId, Map<String, String> data);

    void remove(String sessionId);
}
