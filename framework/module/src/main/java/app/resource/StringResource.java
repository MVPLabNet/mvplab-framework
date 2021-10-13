package app.resource;

import com.google.common.base.Charsets;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author chi
 */
public class StringResource implements Resource {
    private final String path;
    private final String content;
    private final long timestamp;

    public StringResource(String path, String content) {
        this(path, content, System.currentTimeMillis());
    }

    public StringResource(String path, String content, long timestamp) {
        this.path = path;
        this.content = content;
        this.timestamp = timestamp;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public InputStream openStream() {
        return new ByteArrayInputStream(content.getBytes(Charsets.UTF_8));
    }

    @Override
    public long lastModified() {
        return timestamp;
    }

    @Override
    public long length() {
        return content.getBytes(Charsets.UTF_8).length;
    }
}
