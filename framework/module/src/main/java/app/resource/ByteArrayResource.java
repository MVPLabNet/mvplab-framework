package app.resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author chi
 */
public class ByteArrayResource implements Resource {
    private final String path;
    private final byte[] bytes;
    private final long timestamp;

    public ByteArrayResource(String path, byte[] bytes) {
        this(path, bytes, System.currentTimeMillis());
    }

    public ByteArrayResource(String path, byte[] bytes, long timestamp) {
        this.path = path;
        this.bytes = bytes;
        this.timestamp = timestamp;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public InputStream openStream() {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public long lastModified() {
        return timestamp;
    }

    @Override
    public long length() {
        return bytes.length;
    }
}
