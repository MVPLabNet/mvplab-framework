package app.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author chi
 */
public class FileResource implements Resource {
    private final String path;
    private final Path file;

    public FileResource(String path, Path file) {
        this.path = path;
        this.file = file;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public InputStream openStream() {
        try {
            return Files.newInputStream(file);
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    @Override
    public long lastModified() {
        return file().lastModified();
    }

    @Override
    public long length() {
        return file().length();
    }

    public File file() {
        return file.toFile();
    }
}
