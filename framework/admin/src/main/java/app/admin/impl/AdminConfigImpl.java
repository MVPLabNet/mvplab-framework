package app.admin.impl;

import app.admin.AdminConfig;
import app.admin.ConsoleBundle;
import app.admin.ConsoleBundleConfig;
import app.admin.impl.service.Console;
import app.web.WebConfig;

/**
 * @author chi
 */
public class AdminConfigImpl implements AdminConfig {
    private final WebConfig webConfig;
    private final Console console;

    public AdminConfigImpl(WebConfig webConfig, Console console) {
        this.webConfig = webConfig;
        this.console = console;
    }

    @Override
    public <T> AdminConfig controller(Class<T> controllerClass) {
        webConfig.controller(controllerClass);
        return this;
    }

    @Override
    public AdminConfig install(ConsoleBundle consoleBundle) {
        console.install(consoleBundle);
        return this;
    }

    @Override
    public ConsoleBundleConfig bundle(String name) {
        return new ConsoleBundleConfigImpl(console.bundle(name));
    }

    @Override
    public AdminConfig setDefaultPath(String path) {
        console.setDefaultPath(path);
        return this;
    }
}