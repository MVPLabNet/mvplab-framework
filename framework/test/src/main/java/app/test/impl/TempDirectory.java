package app.test.impl;


import com.google.common.io.Files;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class TempDirectory {
    private final Path path;

    public TempDirectory() {
        this(Files.createTempDir().toPath());
    }

    public TempDirectory(Path dir) {
        this.path = dir;
    }

    public Path root() {
        return path;
    }

    public void delete() throws IOException {
        java.nio.file.Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                java.nio.file.Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }
                java.nio.file.Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}