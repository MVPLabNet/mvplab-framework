package app.web;

import app.AbstractModule;
import app.Binder;
import app.Configurable;
import app.resource.ClasspathResourceRepository;
import app.resource.FileResourceRepository;
import app.resource.ResourceRepository;
import app.template.TemplateEngine;
import app.web.impl.ClientInfoContextProvider;
import app.web.impl.LocalSessionRepository;
import app.web.impl.RedisSessionRepository;
import app.web.impl.RequestInfoContextProvider;
import app.web.impl.ResourceMessageBodyWriter;
import app.web.impl.SessionInfoContextProvider;
import app.web.impl.SessionRepository;
import app.web.impl.SitemapService;
import app.web.impl.TemplateMessageBodyWriter;
import app.web.impl.ThemedResourceRepository;
import app.web.impl.WebConfigImpl;
import app.web.impl.WebFilter;
import app.web.impl.WebTemplateFunctions;
import app.web.impl.controller.FaviconResourceController;
import app.web.impl.controller.HealthCheckController;
import app.web.impl.controller.NodeModulesResourceController;
import app.web.impl.controller.RobotsResourceController;
import app.web.impl.controller.SitemapController;
import app.web.impl.controller.SitemapIndexController;
import app.web.impl.controller.StaticResourceController;
import app.web.impl.controller.SwitchLanguageWebController;
import app.web.impl.controller.ThemeStaticResourceController;
import app.web.impl.exception.BadRequestWebExceptionMapper;
import app.web.impl.exception.DefaultWebExceptionMapper;
import app.web.impl.exception.ForbiddenWebExceptionMapper;
import app.web.impl.exception.NotAuthorizedWebExceptionMapper;
import app.web.impl.exception.NotFoundWebExceptionMapper;
import app.web.impl.exception.ValidationWebExceptionMapper;
import app.web.impl.processor.HrefElementProcessor;
import app.web.impl.processor.SrcElementProcessor;
import app.web.impl.processor.SrcsetElementProcessor;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author chi
 */
public final class WebModule extends AbstractModule implements Configurable<WebConfig> {
    WebOptions webOptions;
    WebRoot webRoot;
    SitemapService sitemapService;
    TemplateEngine templateEngine;

    @Override
    protected void configure() {
        webOptions = options("web", WebOptions.class);
        bind(WebOptions.class).toInstance(webOptions);

        ResourceRepository[] repositories = repositories(app().dataDir(), webOptions.roots);
        webRoot = new WebRoot(repositories);

        bind(WebRoot.class).toInstance(webRoot);
        templateEngine = new TemplateEngine(repositories).setCacheEnabled(webOptions.cacheEnabled);

        if (!Strings.isNullOrEmpty(webOptions.theme)) {
            ThemedResourceRepository themedResourceRepository = new ThemedResourceRepository(webOptions.theme, repositories);
            templateEngine.addRepository(themedResourceRepository);
            webRoot.add(themedResourceRepository);
        }

        templateEngine.addElementProcessor(new HrefElementProcessor(webOptions.cdnBaseURLs, webRoot, webOptions.inlineResourceEnabled));
        templateEngine.addElementProcessor(new SrcElementProcessor(webOptions.cdnBaseURLs, webRoot, webOptions.inlineResourceEnabled));
        templateEngine.addElementProcessor(new SrcsetElementProcessor(webRoot));
        templateEngine.addFunctions(null, new WebTemplateFunctions(app().message()));
        bind(TemplateEngine.class).toInstance(templateEngine);
        if (webOptions.session.redis == null) {
            bind(SessionRepository.class).toInstance(new LocalSessionRepository(webOptions.session));
        } else {
            bind(SessionRepository.class).toInstance(new RedisSessionRepository(webOptions.session));
        }
        bind(SessionManager.class);

        WebConfig webConfig = module(WebModule.class);
        webConfig.controller(HealthCheckController.class);
        webConfig.controller(StaticResourceController.class);
        webConfig.controller(ThemeStaticResourceController.class);
        webConfig.controller(FaviconResourceController.class);
        webConfig.controller(RobotsResourceController.class);
        webConfig.controller(NodeModulesResourceController.class);
        webConfig.controller(SwitchLanguageWebController.class);
        webConfig.controller(SitemapController.class);
        webConfig.controller(SitemapIndexController.class);

//        webConfig.bind(UserInfo.class, UserInfoContextProvider.class);
        webConfig.bind(ClientInfo.class, ClientInfoContextProvider.class);
        webConfig.bind(SessionInfo.class, SessionInfoContextProvider.class);
        webConfig.bind(RequestInfo.class, RequestInfoContextProvider.class);

        webConfig.bindResponseFilter(requestInjection(new WebFilter()));
        webConfig.bindMessageBodyWriter(requestInjection(new TemplateMessageBodyWriter()));
        webConfig.bindMessageBodyWriter(requestInjection(new ResourceMessageBodyWriter()));

        app().register(NotAuthorizedWebExceptionMapper.class);
        app().register(NotFoundWebExceptionMapper.class);
        app().register(ValidationWebExceptionMapper.class);
        app().register(ForbiddenWebExceptionMapper.class);
        app().register(BadRequestWebExceptionMapper.class);
        app().register(DefaultWebExceptionMapper.class);

        WebCache sitemapCache = webConfig.createCache("sitemap");
        sitemapService = new SitemapService(app().baseURL(), sitemapCache);
        bind(SitemapService.class).toInstance(sitemapService);

        message("conf/messages/web");
    }

    private ResourceRepository[] repositories(Path dataDir, List<String> rootPaths) {
        List<ResourceRepository> repositories = Lists.newArrayList();
        repositories.add(new ClasspathResourceRepository("web"));
        repositories.add(new FileResourceRepository(dataDir.resolve("web")));
        Path root = app().dir();
        if (root != null) {
            repositories.add(new FileResourceRepository(root.resolve("web")));
        }
        for (String rootPath : rootPaths) {
            Path path = Paths.get(rootPath);
            if (path.isAbsolute()) {
                repositories.add(new FileResourceRepository(path));
            } else {
                if (root != null) {
                    repositories.add(new FileResourceRepository(root.resolve(path)));
                }
            }
        }
        return repositories.toArray(new ResourceRepository[0]);
    }

    @Override
    public WebConfig configurator(AbstractModule module, Binder binder) {
        return new WebConfigImpl(binder, webOptions, templateEngine, sitemapService, app());
    }
}
