package app.undertow;

import app.AbstractModule;
import app.app.JerseyApp;
import app.undertow.websocket.WebSocketModule;
import app.undertow.websocket.WebSocketService;
import app.util.exception.Errors;
import com.google.common.collect.Lists;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.Options;

import java.util.List;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.websocket;

/**
 * @author chi
 */
public class UndertowServer {
    private final Logger logger = LoggerFactory.getLogger(UndertowServer.class);
    private final List<UndertowHttpContainer> containers = Lists.newArrayList();
    private JerseyApp app;
    private Undertow undertow;
    private final UndertowOptions options;

    public UndertowServer() {
        options = new UndertowOptions();
    }

    public UndertowServer(UndertowOptions options) {
        this.options = options;
    }

    public UndertowServer install(JerseyApp app) {
        if (this.app != null) {
            throw Errors.internalError("App is installed");
        }
        this.app = app;
        return this;
    }

    public void start() {
        if (Boolean.TRUE.equals(options.webSocketEnabled)) {
            if (!app.isInstalled(WebSocketModule.class)) {
                app.install(new WebSocketModule());
            }
        }
        app.configure();

        UndertowHttpContainer container = new UndertowHttpContainer(app);
        containers.add(container);
        container.getApplicationHandler().onStartup(container);

        Undertow.Builder builder = Undertow.builder();
        builder.addHttpListener(options.port, options.host);
        // set tcp back log larger, also requires to update kernel, e.g. sysctl -w net.core.somaxconn=1024 && sysctl -w net.ipv4.tcp_max_syn_backlog=4096
        builder.setSocketOption(Options.BACKLOG, 4096);
        builder.setServerOption(io.undertow.UndertowOptions.DECODE_URL, Boolean.FALSE);
        builder.setServerOption(io.undertow.UndertowOptions.ENABLE_HTTP2, Boolean.TRUE);
        builder.setServerOption(io.undertow.UndertowOptions.ENABLE_RFC6265_COOKIE_VALIDATION, Boolean.TRUE);
        // since we don't use Expires or Last- Modified header, so it's not necessary to set Date header, for cache, prefer cache-control/max-age
        // refer to https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.18.1
        builder.setServerOption(io.undertow.UndertowOptions.ALWAYS_SET_DATE, Boolean.FALSE);
        builder.setServerOption(io.undertow.UndertowOptions.ALWAYS_SET_KEEP_ALIVE, Boolean.FALSE);
        // set tcp idle timeout to 620s, by default AWS ALB uses 60s, GCloud LB uses 600s, since it is always deployed with LB, longer timeout doesn't hurt
        // refer to https://cloud.google.com/load-balancing/docs/https/#timeouts_and_retries
        // refer to https://docs.aws.amazon.com/elasticloadbalancing/latest/application/application-load-balancers.html#connection-idle-timeout
        builder.setServerOption(io.undertow.UndertowOptions.NO_REQUEST_TIMEOUT, 620 * 1000);     // 620s
        builder.setServerOption(io.undertow.UndertowOptions.SHUTDOWN_TIMEOUT, 10 * 1000);        // 10s
        builder.setServerOption(io.undertow.UndertowOptions.MAX_ENTITY_SIZE, 10L * 1024 * 1024);
        builder.setServerOption(io.undertow.UndertowOptions.MULTIPART_MAX_ENTITY_SIZE, 10L * 1024 * 1024);

        UndertowIOHandler handler = new UndertowIOHandler(container);
        PathHandler pathHandler = path();
        pathHandler.addPrefixPath(container.path(), handler);

        if (Boolean.TRUE.equals(options.webSocketEnabled)) {
            AbstractModule webSocketModule = app.module(WebSocketModule.class);
            WebSocketService webSocketService = webSocketModule.require(WebSocketService.class);
            pathHandler
                .addPrefixPath("/websocket/", websocket(webSocketService.webSocketConnectionCallback()));
        }

        builder.setHandler(pathHandler);
        undertow = builder.build();
        logger.info("server started, host={}, port={}, dataDir={}, appDir={}", options.host, options.port, app.dataDir(), app.dir());
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        this.undertow.start();
    }

    public void stop() {
        if (undertow != null) {
            undertow.stop();
        }
        for (UndertowHttpContainer container : containers) {
            container.getApplicationHandler().onShutdown(container);
        }
        logger.info("server stopped");
    }
}
