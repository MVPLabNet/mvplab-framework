package app.resource;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author chi
 */
public class ClasspathResource implements Resource {
    private final String path;
    private final String fullPath;
    private final long timestamp;

    public ClasspathResource(String path, String fullPath) {
        this.path = path;
        this.fullPath = fullPath;
        timestamp = System.currentTimeMillis();
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public InputStream openStream() {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(fullPath);
    }

    @Override
    public long lastModified() {
        return timestamp;
    }

    @Override
    public long length() {
        try (InputStream inputStream = openStream()) {
            return ByteStreams.toByteArray(inputStream).length;
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }
}
