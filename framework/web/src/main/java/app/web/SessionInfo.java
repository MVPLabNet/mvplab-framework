package app.web;

import java.util.Optional;

/**
 * @author chi
 */
public interface SessionInfo {
    String id();

    Optional<String> get(String key);

    void put(String key, String value);

    void delete(String key);

    void invalidate();
}
