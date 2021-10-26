package app.test;


import app.AbstractModule;
import app.ApplicationException;
import app.database.Database;
import app.database.DatabaseModule;
import app.test.impl.TempDirectory;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Deque;
import java.util.Set;

/**
 * @author chi
 */
public class AppExtension implements TestInstancePostProcessor, AfterTestExecutionCallback, AfterAllCallback {
    private static final String HSQL_RESET_SQL = "TRUNCATE SCHEMA public AND COMMIT";
    private final Logger logger = LoggerFactory.getLogger(AppExtension.class);

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        ExtensionContext.Store store = context.getStore(ExtensionContext.Namespace.create(AppExtension.class, MockApp.class));
        Install install = testInstance.getClass().getDeclaredAnnotation(Install.class);
        if (install == null) {
            throw new ApplicationException("missing @Install annotation");
        }
        MockApp app = (MockApp) store.getOrComputeIfAbsent(MockApp.class, type -> createApp(install.value()));
        app.inject(testInstance);
    }

    private MockApp createApp(Class<? extends AbstractModule>... moduleClasses) {
        logger.info("create mock server, modules={}", moduleClasses);
        MockApp app = new MockApp(Files.createTempDir().toPath());
        Deque<Class<? extends AbstractModule>> toInstall = Lists.newLinkedList(Arrays.asList(moduleClasses));
        Set<Class<? extends AbstractModule>> installed = Sets.newHashSet();
        while (!toInstall.isEmpty()) {
            Class<? extends AbstractModule> moduleClass = toInstall.poll();
            try {
                if (!installed.contains(moduleClass)) {
                    installed.add(moduleClass);

                    AbstractModule module = moduleClass.getDeclaredConstructor().newInstance();
                    app.install(module);
                    for (Class<? extends AbstractModule> dependency : module.dependencies()) {
                        if (!installed.contains(dependency)) {
                            toInstall.push(dependency);
                        }
                    }
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new ApplicationException("failed to create module, moduleClass={}", moduleClass.getCanonicalName(), e);
            }
        }
        app.start();
        return app;
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        ExtensionContext.Store store = context.getStore(ExtensionContext.Namespace.create(AppExtension.class, MockApp.class));
        MockApp app = (MockApp) store.get(MockApp.class);
        if (app.isInstalled(DatabaseModule.class)) {
            AbstractModule module = app.module(DatabaseModule.class);
            Database database = module.require(Database.class);
            try {
                logger.info("reset database");
                final Connection connection = database.dataSource().getConnection();
                connection.createStatement().executeUpdate(HSQL_RESET_SQL);
                connection.commit();
                connection.close();
            } catch (Exception e) {
                logger.error("failed to reset database", e);
            }
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        ExtensionContext.Store store = context.getStore(ExtensionContext.Namespace.create(AppExtension.class, MockApp.class));
        MockApp app = (MockApp) store.get(MockApp.class);
        if (app != null) {
            app.stop();
            new TempDirectory(app.dataDir()).delete();
        }
    }
}
