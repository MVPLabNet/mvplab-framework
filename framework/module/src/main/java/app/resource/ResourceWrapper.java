package app.resource;

import java.io.InputStream;

/**
 * @author chi
 */
public class ResourceWrapper implements Resource {
    private final String path;
    private final Resource resource;

    public ResourceWrapper(String path, Resource resource) {
        this.path = path;
        this.resource = resource;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public InputStream openStream() {
        return resource.openStream();
    }

    @Override
    public long lastModified() {
        return resource.lastModified();
    }

    @Override
    public long length() {
        return resource.length();
    }
}
