package app.resource;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * @author chi
 */
public interface Resource {
    @Deprecated
    static Resource classpath(String path) {
        return new ClasspathResource(path, path);
    }

    @Deprecated
    static Resource file(Path path) {
        return new FileResource(path.toString(), path);
    }

    String path();

    InputStream openStream();

    default String hash() {
        try (InputStream inputStream = openStream()) {
            byte[] bytes = ByteStreams.toByteArray(inputStream);
            return Hashing.adler32().hashBytes(bytes).toString();
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    long lastModified();

    default long length() {
        return toByteArray().length;
    }

    default String toText(Charset charset) {
        return new String(toByteArray(), charset);
    }

    default byte[] toByteArray() {
        try (InputStream in = openStream()) {
            return ByteStreams.toByteArray(in);
        } catch (IOException e) {
            throw new ResourceException("failed to ", e);
        }
    }
}
