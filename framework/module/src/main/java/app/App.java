package app;

import app.resource.ResourceRepository;
import app.util.i18n.CompositeMessageBundle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

/**
 * @author chi
 */

public interface App {
    String name();

    boolean isAPIEnabled();

    boolean isAdminEnabled();

    boolean isWebEnabled();

    app.Environment env();

    String baseURL();

    String imageURL();

    String description();

    String language();

    List<AbstractModule> modules();

    List<String> supportLanguages();

    void register(Object object);

    void register(Class<?> object);

    Path dataDir();

    Path dir();

    app.Profile profile();

    <T> T require(Type type, Annotation qualifier);

    Binder binder(AbstractModule module);

    @Deprecated
    CompositeMessageBundle message();

    @Deprecated
    ResourceRepository resource();

    <K, T extends AbstractModule & Configurable<K>> K config(AbstractModule module, Class<T> type);
}
