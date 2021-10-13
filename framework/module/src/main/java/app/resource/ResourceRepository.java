package app.resource;

import java.util.Optional;

/**
 * @author chi
 */
public interface ResourceRepository {
    Optional<Resource> get(String path);
}
