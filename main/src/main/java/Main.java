import app.AbstractModule;
import app.app.JerseyApp;
import app.demo.DemoModule;
import app.demo.DemoServiceModule;
import app.undertow.UndertowServer;

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
        app.install(new DemoModule());
        app.install(new DemoServiceModule());
        ServiceLoader.load(AbstractModule.class).forEach(app::install);
        UndertowServer undertowServer = new UndertowServer();
        undertowServer.install(app);
        undertowServer.start();
    }
}
