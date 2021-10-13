package app.web.impl;

import app.ApplicationException;
import app.resource.FileResource;
import app.resource.Resource;
import app.resource.ResourceException;
import app.web.WebCache;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

public class WebCacheImpl implements WebCache {
    private final Path dir;

    public WebCacheImpl(Path dir) {
        this.dir = dir;
    }

    @Override
    public Path path() {
        return dir;
    }

    @Override
    public Optional<Resource> get(String path) {
        String resourcePath = path.startsWith("/") ? path.substring(1) : path;
        File file = dir.resolve(resourcePath).toFile();
        if (file.exists() && file.isFile()) {
            return Optional.of(new FileResource(path, file.toPath()));
        }
        return Optional.empty();
    }

    @Override
    public void create(Resource resource) {
        try {
            String resourcePath = resource.path().startsWith("/") ? resource.path().substring(1) : resource.path();
            Path path = dir.resolve(resourcePath);
            Files.createDirectories(path.getParent());
            Files.write(path, resource.toByteArray());
            path.toFile().setLastModified(resource.lastModified());
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    @Override
    public void delete(String path) {
        String resourcePath = path.startsWith("/") ? path.substring(1) : path;
        File file = dir.resolve(resourcePath).toFile();
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void deleteAll() {
        try {
            if (!dir.toFile().exists()) {
                return;
            }
            Files.walkFileTree(dir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult postVisitDirectory(
                    Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(
                    Path file, BasicFileAttributes attrs)
                    throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }
}