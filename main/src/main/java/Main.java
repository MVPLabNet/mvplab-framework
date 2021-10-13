import app.AbstractModule;
import app.app.JerseyApp;
import app.undertow.UndertowServer;
import app.web.AbstractWebModule;

import javax.ws.rs.GET;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ServiceLoader;

/**
 * @author chi
 */
public class Main {
    public static void main(String[] args) {
        Path path = Paths.get(System.getProperty("user.dir")).resolve("main/src/main/dist");
        JerseyApp app = new JerseyApp(path);
        app.install(new AbstractWebModule() {
            @Override
            protected void configure() {
                bindController(HelloWorldController.class);
            }
        });
        ServiceLoader.load(AbstractModule.class).forEach(app::install);
        UndertowServer undertowServer = new UndertowServer();
        undertowServer.install(app);
        undertowServer.start();
    }

    @javax.ws.rs.Path("/hello")
    public static class HelloWorldController {
        @GET
        public String hello() {
            return "Hello MVPLab";
        }
    }
}
