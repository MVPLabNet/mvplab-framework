package app.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * @author chi
 */
public class ClasspathResourceRepository implements ResourceRepository {
    private final String basePath;

    public ClasspathResourceRepository(String basePath) {
        this.basePath = "".equals(basePath) ? "" : !basePath.endsWith("/") ? basePath + '/' : basePath;
    }

    @Override
    public Optional<Resource> get(String path) {
        ClasspathResource resource = new ClasspathResource(path, fullPath(path));

        try (InputStream inputStream = resource.openStream()) {
            return inputStream == null ? Optional.empty() : Optional.of(resource);
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    private String fullPath(String path) {
        return basePath + (path.length() > 0 && path.charAt(0) == '/' ? path.substring(1) : path);
    }
}
