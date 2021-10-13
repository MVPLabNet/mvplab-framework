package app.test.impl;

import com.google.common.io.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author chi
 */
class TempDirectoryTest {
    TempDirectory tempDirectory;

    @BeforeEach
    void setup() throws IOException {
        File dir = Files.createTempDir();
        new File(dir, "1.txt").createNewFile();
        new File(dir, "dir1").mkdir();
        new File(dir, "dir1/2.txt").createNewFile();
        tempDirectory = new TempDirectory(dir.toPath());
    }

    @Test
    void test() throws IOException {
        tempDirectory.delete();
        Path root = tempDirectory.root();
        assertFalse(root.toFile().exists());
    }
}