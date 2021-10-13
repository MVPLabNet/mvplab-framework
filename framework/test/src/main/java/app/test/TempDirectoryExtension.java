package app.test;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.reflect.Parameter;
import java.nio.file.Path;

public class TempDirectoryExtension implements ParameterResolver, AfterAllCallback {
    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        ExtensionContext.Store store = context.getStore(ExtensionContext.Namespace.create(TempDirectoryExtension.class, app.test.impl.TempDirectory.class));
        app.test.impl.TempDirectory tempDirectory = (app.test.impl.TempDirectory) store.get(app.test.impl.TempDirectory.class);
        if (tempDirectory != null) {
            tempDirectory.delete();
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        return parameter.getAnnotation(TempDirectory.class) != null && Path.class.equals(parameter.getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        ExtensionContext.Store store = extensionContext.getStore(ExtensionContext.Namespace.create(TempDirectoryExtension.class, app.test.impl.TempDirectory.class));
        app.test.impl.TempDirectory tempDirectory = (app.test.impl.TempDirectory) store.getOrComputeIfAbsent(app.test.impl.TempDirectory.class, key -> new app.test.impl.TempDirectory());
        return tempDirectory.root();
    }
}