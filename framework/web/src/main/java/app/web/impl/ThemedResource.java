package app.web.impl;

import app.resource.Resource;

import java.io.InputStream;

/**
 * @author chi
 */
public class ThemedResource implements Resource {
    private final String path;
    private final Resource raw;

    public ThemedResource(String path, Resource raw) {
        this.path = path;
        this.raw = raw;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public InputStream openStream() {
        return raw.openStream();
    }

    @Override
    public long lastModified() {
        return raw.lastModified();
    }

    public Resource raw() {
        return raw;
    }
}
