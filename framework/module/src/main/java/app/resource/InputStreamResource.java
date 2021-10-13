package app.resource;

import java.io.InputStream;

/**
 * @author chi
 */
public class InputStreamResource implements Resource {
    private final String path;
    private final InputStream inputStream;
    private final long timestamp;

    public InputStreamResource(String path, InputStream inputStream) {
        this(path, inputStream, System.currentTimeMillis());
    }

    public InputStreamResource(String path, InputStream inputStream, long timestamp) {
        this.path = path;
        this.inputStream = inputStream;
        this.timestamp = timestamp;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public InputStream openStream() {
        return inputStream;
    }

    @Override
    public long lastModified() {
        return timestamp;
    }

    @Override
    public long length() {
        throw new ResourceException("not support");
    }
}
