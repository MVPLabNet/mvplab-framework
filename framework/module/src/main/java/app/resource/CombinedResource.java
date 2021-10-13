package app.resource;

import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * @author chi
 */
public class CombinedResource implements Resource {
    private final Logger logger = LoggerFactory.getLogger(CombinedResource.class);

    private final String path;
    private final List<String> resourcePaths;
    private final ResourceRepository repository;

    public CombinedResource(String path, List<String> resourcePaths, ResourceRepository repository) {
        this.path = path;
        this.resourcePaths = resourcePaths;
        this.repository = repository;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public InputStream openStream() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (String resourcePath : resourcePaths) {
            Optional<Resource> resource = repository.get(resourcePath);
            if (!resource.isPresent()) {
                logger.warn("missing resource file, path={}", resourcePath);
            } else {
                try {
                    outputStream.write(resource.get().toByteArray());
                    outputStream.write("\n".getBytes(Charsets.UTF_8));
                } catch (IOException e) {
                    throw new ResourceException(e);
                }
            }
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    @Override
    public long lastModified() {
        long lastModified = 0;
        for (String resourcePath : resourcePaths) {
            Optional<Resource> resource = repository.get(resourcePath);
            if (resource.isPresent()) {
                long current = resource.get().lastModified();
                if (current > lastModified) {
                    lastModified = current;
                }
            }
        }
        return lastModified;
    }

    @Override
    public long length() {
        return -1;
    }
}
