package app.app;

import app.AbstractModule;
import app.App;
import app.Binder;
import app.Configurable;
import app.Environment;
import app.Profile;
import app.app.impl.AppOptions;
import app.app.impl.ModuleBinder;
import app.app.impl.exception.BadRequestExceptionMapper;
import app.app.impl.exception.ForbiddenExceptionMapper;
import app.app.impl.exception.NotAuthorizedExceptionMapper;
import app.app.impl.exception.NotFoundExceptionMapper;
import app.app.impl.exception.ValidationExceptionMapper;
import app.app.impl.exception.WebApplicationExceptionMapper;
import app.resource.ClasspathResourceRepository;
import app.resource.CompositeResourceRepository;
import app.resource.FileResourceRepository;
import app.resource.ResourceRepository;
import app.util.JSON;
import app.util.exception.Errors;
import app.util.i18n.CompositeMessageBundle;
import app.util.i18n.MessageBundle;
import app.util.type.Types;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.general.internal.MessageInterpolatorImpl;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.glassfish.jersey.server.validation.ValidationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Configuration;
import javax.validation.Validation;
import javax.ws.rs.core.Application;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author chi
 */
public class JerseyApp extends Application implements App {
    private final Logger logger = LoggerFactory.getLogger(JerseyApp.class);

    private final Map<Class<? extends AbstractModule>, ModuleInfo> modules = Maps.newHashMap();
    private List<Class<? extends AbstractModule>> orderedModules;

    private final List<Object> singletons = Lists.newArrayList();
    private final Map<String, Object> resourceSingletons = Maps.newHashMap();
    private final List<Class<?>> classes = Lists.newArrayList();
    private final Map<String, Class<?>> resourceClasses = Maps.newHashMap();
    private final Map<String, Object> properties = Maps.newHashMap();
    private final Map<Class<? extends AbstractModule>, ModuleBinder> moduleBinders = Maps.newHashMap();

    final Path dataDir;
    final AppOptions options;
    final Profile profile;
    final ResourceRepository repository;
    final CompositeMessageBundle compositeMessageBundle;

    ServiceLocator serviceLocator;
    ModuleBinder binder;

    public JerseyApp(Path dataDir, Profile profile) {
        this.dataDir = dataDir;
        this.profile = profile;
        repository = new CompositeResourceRepository(new FileResourceRepository(dataDir), new ClasspathResourceRepository(""));
        options = profile.options("app", AppOptions.class);
        if (options.language == null) {
            options.language = Locale.getDefault().toLanguageTag();
        } else {
            Locale.setDefault(Locale.forLanguageTag(options.language));
        }
        if (options.supportLanguages == null || options.supportLanguages.isEmpty()) {
            options.supportLanguages = Lists.newArrayList(options.language);
        }
        if (!options.baseURL.endsWith("/")) {
            options.baseURL = options.baseURL + "/";
        }
        compositeMessageBundle = new CompositeMessageBundle();
    }

    public JerseyApp(Path dataDir) {
        this(dataDir, YAMLProfile.load(dataDir));
    }

    public JerseyApp() {
        this(new File(".").toPath());
    }

    public Path dataDir() {
        return dataDir;
    }

    @Override
    public Path dir() {
        String appDir = System.getProperty("app.dir", null);
        if (appDir == null) {
            return Path.of(System.getProperty("user.dir"));
        }
        return Path.of(appDir);
    }

    public String name() {
        return options.name;
    }

    public String baseURL() {
        return options.baseURL;
    }

    public String language() {
        return options.language;
    }

    public List<String> supportLanguages() {
        return options.supportLanguages;
    }

    public <T> T options(String name, Class<T> optionClass) {
        return profile.options(name, optionClass);
    }

    public Environment env() {
        return options.env == null ? Environment.PROD : options.env;
    }

    public Profile profile() {
        return profile;
    }

    @Override
    public <T> T require(Type type, Annotation qualifier) {
        if (qualifier == null) {
            return serviceLocator.getService(type);
        }
        return serviceLocator.getService(type, qualifier);
    }

    @Override
    public Binder binder(AbstractModule module) {
        return moduleBinders.get(module.getClass());
    }

    public ServiceLocator serviceLocator() {
        if (serviceLocator == null) {
            throw Errors.internalError("service locator is not available during configure phase");
        }
        return serviceLocator;
    }

    public ResourceRepository resource() {
        return repository;
    }

    public CompositeMessageBundle message() {
        return compositeMessageBundle;
    }

    @Override
    public Set<Object> getSingletons() {
        ImmutableSet.Builder<Object> builder = ImmutableSet.builder();
        builder.addAll(singletons);
        builder.addAll(resourceSingletons.values());
        return builder.build();
    }

    @Override
    public Set<Class<?>> getClasses() {
        ImmutableSet.Builder<Class<?>> builder = ImmutableSet.builder();
        builder.addAll(classes);
        builder.addAll(resourceClasses.values());
        return builder.build();
    }

    @Override
    public Map<String, Object> getProperties() {
        return ImmutableMap.copyOf(properties);
    }

    public void inject(Object instance) {
        serviceLocator().inject(instance);
    }

    public final void register(Object singleton) {
        checkNotNull(singleton, "resource can't null");
        if (isResource(singleton.getClass())) {
            javax.ws.rs.Path path = singleton.getClass().getDeclaredAnnotation(javax.ws.rs.Path.class);
            if (resourceSingletons.containsKey(path.value())) {
                logger.info("override resource, path={}, original={}, resource={}", path.value(), resourceSingletons.get(path.value()), singleton.getClass());
            }
            resourceSingletons.put(path.value(), singleton);
        } else {
            singletons.add(singleton);
        }
    }

    public final void register(Class<?> type) {
        checkNotNull(type, "resource can't null");
        if (isResource(type)) {
            javax.ws.rs.Path path = type.getDeclaredAnnotation(javax.ws.rs.Path.class);
            if (resourceClasses.containsKey(path.value())) {
                logger.info("override resource, path={}, original={}, resource={}", path.value(), resourceClasses.get(path.value()), type);
            }
            resourceClasses.put(path.value(), type);
        } else {
            classes.add(type);
        }
    }

    private boolean isResource(Class<?> resourceClass) {
        return resourceClass.isAnnotationPresent(javax.ws.rs.Path.class);
    }

    public final void property(String name, Object value) {
        properties.put(name, value);
    }

    public List<AbstractModule> modules() {
        ImmutableList.Builder<AbstractModule> builder = ImmutableList.builder();
        for (Class<? extends AbstractModule> moduleClass : orderedModules()) {
            ModuleInfo moduleInfo = moduleInfo(moduleClass);
            if (!moduleInfo.isOverride()) {
                builder.add(moduleInfo.module);
            }
        }
        return builder.build();
    }

    public App install(AbstractModule module) {
        if (module == null) {
            throw Errors.internalError("module can't be null");
        }
        if (isInstalled(module.getClass())) {
            throw Errors.internalError("module installed, type={}", module.getClass().getCanonicalName());
        }

        List<Class<? extends AbstractModule>> parents = Lists.newArrayList();
        Class<? extends AbstractModule> implementation = module.getClass();

        for (Map.Entry<Class<? extends AbstractModule>, ModuleInfo> entry : modules.entrySet()) {
            Class<? extends AbstractModule> moduleClass = entry.getKey();
            if (moduleClass.isAssignableFrom(implementation)) {
                parents.add(moduleClass);
            } else if (implementation.isAssignableFrom(moduleClass)) {
                implementation = moduleClass;
            }
        }

        ModuleInfo moduleInfo = new ModuleInfo();
        moduleInfo.module = module;
        moduleInfo.implementation = implementation;
        if (module.getClass().equals(implementation)) {
            for (Class<? extends AbstractModule> moduleClass : parents) {
                ModuleInfo overrideModuleInfo = modules.get(moduleClass);
                overrideModuleInfo.implementation = implementation;
            }
        }
        modules.put(module.getClass(), moduleInfo);
        return this;
    }

    @SuppressWarnings("unchecked")
    public AbstractModule module(Class<? extends AbstractModule> moduleClass) {
        ModuleInfo moduleInfo = moduleInfo(moduleClass);
        return moduleInfo.isOverride() ? module(moduleInfo.implementation) : moduleInfo.module;
    }

    private ModuleInfo moduleInfo(Class<? extends AbstractModule> moduleClass) {
        ModuleInfo moduleInfo = modules.get(moduleClass);
        if (moduleInfo == null) {
            throw Errors.internalError("missing module, type={}", moduleClass);
        }
        return moduleInfo;
    }

    public boolean isInstalled(Class<? extends AbstractModule> moduleClass) {
        ModuleInfo moduleInfo = modules.get(moduleClass);
        return moduleInfo != null;
    }

    public void configure() {
        binder = new ModuleBinder();

        binder.bind(App.class).toInstance(this);

        binder.bind(Configuration.class).toInstance(Validation.byDefaultProvider().configure()
            .messageInterpolator(new MessageInterpolatorImpl())
            .addProperty("hibernate.validator.fail_fast", "true"));
        register(ValidationFeature.class);

        binder.bind(MessageBundle.class).toInstance(compositeMessageBundle);

        JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
        jacksonProvider.setMapper(JSON.OBJECT_MAPPER);
        register(jacksonProvider);
        register(JacksonFeature.class);

        register(binder.raw());
        register(new DefaultContainerLifecycleListener(this));

        register(new AppEventListener());

        register(ForbiddenExceptionMapper.class);
        register(NotAuthorizedExceptionMapper.class);
        register(NotFoundExceptionMapper.class);
        register(ValidationExceptionMapper.class);
        register(BadRequestExceptionMapper.class);
        register(WebApplicationExceptionMapper.class);

        validate();

        StringBuilder b = new StringBuilder();
        modules.forEach((moduleClass, moduleInfo) -> {
            b.append("\n\t").append(moduleClass.getCanonicalName());
            if (moduleInfo.isOverride()) {
                b.append(" => ").append(moduleInfo.implementation.getCanonicalName());
            }
        });
        logger.info("install modules: {}", b.toString());
        for (AbstractModule module : modules()) {
            try {
                ModuleBinder binder = new ModuleBinder(this.binder);
                moduleBinders.put(module.getClass(), binder);
                module.configure(this);
                binder.complete();
            } catch (Exception e) {
                throw Errors.internalError("failed to install module, type={}", module.getClass().getCanonicalName(), e);
            }
        }
    }

    public void validate() {
        Map<Class<? extends AbstractModule>, Set<Class<? extends AbstractModule>>> dependencies = Maps.newHashMap();
        Set<Class<? extends AbstractModule>> modules = Sets.newHashSet(orderedModules());
        for (Class<? extends AbstractModule> moduleClass : modules) {
            if (!dependencies.containsKey(moduleClass)) {
                dependencies.put(moduleClass, Sets.newHashSet());
            }
            for (Class<? extends AbstractModule> dependency : module(moduleClass).dependencies()) {
                dependencies.get(moduleClass).add(dependency);

                if (dependencies.containsKey(dependency)) {
                    dependencies.get(moduleClass).addAll(dependencies.get(dependency));
                }
            }
            if (dependencies.get(moduleClass).contains(moduleClass)) {
                throw Errors.internalError("cycle module dependency, name={}", moduleClass);
            }
        }
    }

    private List<Class<? extends AbstractModule>> orderedModules() {
        if (orderedModules == null) {
            Graph<Class<? extends AbstractModule>> graph = createGraph();
            Deque<Class<? extends AbstractModule>> readyModules = Lists.newLinkedList();
            for (Class<? extends AbstractModule> node : graph.nodes()) {
                if (graph.predecessors(node).isEmpty()) {
                    readyModules.push(node);
                }
            }
            Set<Class<? extends AbstractModule>> visited = Sets.newLinkedHashSet();
            while (!readyModules.isEmpty()) {
                Class<? extends AbstractModule> moduleName = readyModules.pollFirst();
                visited.add(moduleName);
                Set<Class<? extends AbstractModule>> successors = graph.successors(moduleName);
                for (Class<? extends AbstractModule> successor : successors) {
                    boolean ready = true;
                    AbstractModule module = module(successor);
                    for (Class<? extends AbstractModule> dependency : module.dependencies()) {
                        if (!visited.contains(dependency)) {
                            ready = false;
                            break;
                        }
                    }
                    if (ready && !visited.contains(successor)) {
                        readyModules.add(successor);
                    }
                }
            }
            orderedModules = ImmutableList.copyOf(visited);
        }
        return orderedModules;
    }

    private Graph<Class<? extends AbstractModule>> createGraph() {
        MutableGraph<Class<? extends AbstractModule>> graph = GraphBuilder.directed().allowsSelfLoops(false)
            .build();
        for (Map.Entry<Class<? extends AbstractModule>, ModuleInfo> entry : modules.entrySet()) {
            Class<? extends AbstractModule> moduleClass = entry.getKey();
            graph.addNode(moduleClass);
            ModuleInfo moduleInfo = entry.getValue();
            for (Class<? extends AbstractModule> dependency : moduleInfo.dependencies()) {
                graph.addNode(dependency);
                graph.putEdge(dependency, moduleClass);
            }
        }
        return ImmutableGraph.copyOf(graph);
    }

    final void onStartup() {
        Stopwatch w = Stopwatch.createStarted();
        StringBuilder b = new StringBuilder(256);
        b.append('\t');
        resourceClasses.forEach((path, resourceClass) -> {
            b.append(path).append(" => ").append(resourceClass.getCanonicalName()).append("\n\t");
        });
        resourceSingletons.forEach((path, resource) -> {
            b.append(path).append(" => ").append(resource.getClass().getCanonicalName()).append("\n\t");
        });
        logger.info("{} root resources:\n{}", resourceClasses.size() + resourceSingletons.size(), b.toString());
        List<AbstractModule> orderedModules = modules();
        for (AbstractModule module : orderedModules) {
            try {
                ModuleBinder binder = (ModuleBinder) binder(module);
                for (Object instance : binder.injectionRequests()) {
                    inject(instance);
                }
            } catch (Throwable e) {
                throw Errors.internalError("failed to inject instance, type={}", module.getClass().getCanonicalName(), e);
            }
        }
        for (AbstractModule module : orderedModules) {
            try {
                ModuleBinder binder = (ModuleBinder) binder(module);
                for (Runnable startupHook : binder.startupHooks()) {
                    startupHook.run();
                }
            } catch (Throwable e) {
                throw Errors.internalError("failed to start hook, type={}", module.getClass().getCanonicalName(), e);
            }
        }
        for (AbstractModule module : orderedModules) {
            try {
                ModuleBinder binder = (ModuleBinder) binder(module);
                for (Runnable readyHook : binder.readyHooks()) {
                    readyHook.run();
                }
            } catch (Throwable e) {
                throw Errors.internalError("failed to run ready hook, type={}", module.getClass().getCanonicalName(), e);
            }
        }
        logger.info("app started, in {}ms", w.elapsed(TimeUnit.MILLISECONDS));
    }

    final void onShutdown() {
        for (AbstractModule module : modules()) {
            try {
                ModuleBinder binder = (ModuleBinder) binder(module);
                for (Runnable shutdownHook : binder.shutdownHooks()) {
                    shutdownHook.run();
                }
            } catch (Throwable e) {
                throw Errors.internalError("failed to start module, type={}", module.getClass().getCanonicalName(), e);
            }
        }
        logger.info("app stopped");
    }

    @SuppressWarnings("unchecked")
    public <K, T extends AbstractModule & Configurable<K>> K config(AbstractModule module, Class<T> type) {
        List<Class<? extends AbstractModule>> candidates = Lists.newArrayList(recursiveDependencies(module.getClass()));
        candidates.add(module.getClass());
        for (Class<? extends AbstractModule> dependency : candidates) {
            AbstractModule m = module(dependency);
            Type[] interfaces = m.getClass().getGenericInterfaces();
            for (Type interfaceClass : interfaces) {
                if (Types.isGeneric(interfaceClass)) {
                    ParameterizedType parameterizedType = (ParameterizedType) interfaceClass;
                    if (Types.rawClass(parameterizedType).equals(Configurable.class) && m.getClass().equals(type)) {
                        Configurable<K> configurable = (T) module(dependency);
                        return configurable.configurator(module, binder(module));
                    }
                }
            }
        }
        throw Errors.internalError("missing dependency, module={}, require={}", module.getClass(), type);
    }

    public List<Class<? extends AbstractModule>> recursiveDependencies(Class<? extends AbstractModule> moduleClass) {
        Deque<Class<? extends AbstractModule>> deque = Lists.newLinkedList();
        deque.add(moduleClass);
        Set<Class<? extends AbstractModule>> visited = Sets.newHashSet();
        while (!deque.isEmpty()) {
            Class<? extends AbstractModule> current = deque.pollFirst();
            if (!Objects.equals(current, moduleClass)) {
                visited.add(current);
            }
            Class<? extends AbstractModule>[] dependencies = module(current).dependencies();
            for (Class<? extends AbstractModule> dependency : dependencies) {
                if (!visited.contains(dependency)) {
                    deque.add(dependency);
                }
            }
        }
        return Lists.newArrayList(visited);
    }

    public boolean isAPIEnabled() {
        return options.apiEnabled;
    }

    @Override
    public boolean isAdminEnabled() {
        return options.apiEnabled;
    }

    public boolean isWebEnabled() {
        return options.webEnabled;
    }

    public String description() {
        return options.description;
    }

    public String imageURL() {
        return options.imageURL;
    }

    public String host() {
        return options.host;
    }

    public String port() {
        return options.port;
    }

    public class AppEventListener implements ApplicationEventListener {
        RequestEventListener requestEventListener = event -> {
            if (event.getType() == RequestEvent.Type.ON_EXCEPTION) {
                ContainerRequest request = event.getContainerRequest();
                logger.error("request failed, method={}, path={}\n{}", request.getMethod(), request.getAbsolutePath(), Errors.stackTrace(event.getException()));
            }
        };

        @Override
        public void onEvent(ApplicationEvent event) {
        }

        @Override
        public RequestEventListener onRequest(RequestEvent requestEvent) {
            return requestEventListener;
        }
    }

    public static class ModuleInfo {
        public AbstractModule module;
        public Class<? extends AbstractModule> implementation;

        public boolean isOverride() {
            return !module.getClass().equals(implementation);
        }

        @SuppressWarnings("unchecked")
        public Class<? extends AbstractModule>[] dependencies() {
            return isOverride() ? new Class[]{implementation} : module.dependencies();
        }
    }
}
