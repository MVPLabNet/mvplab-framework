package app.web;

import app.resource.Resource;

import java.nio.file.Path;
import java.util.Optional;

/**
 * @author chi
 */
public interface WebCache {
    Path path();

    Optional<Resource> get(String path);

    void create(Resource resource);

    void delete(String path);

    void deleteAll();
}
