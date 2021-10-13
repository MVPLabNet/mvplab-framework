package app.admin;

import app.AbstractModule;
import app.App;
import app.Binder;
import app.Configurable;
import app.admin.impl.AdminConfigImpl;
import app.admin.impl.service.Console;
import app.admin.impl.web.AdminController;
import app.admin.impl.web.AdminStaticResourceController;
import app.admin.impl.web.api.BundleMessageAdminController;
import app.admin.impl.web.api.RoleAdminController;
import app.admin.impl.web.api.SwitchLanguageAdminController;
import app.util.i18n.MessageBundle;
import app.util.i18n.ResourceMessageBundle;
import app.web.AbstractWebModule;
import app.web.WebModule;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author chi
 */
public final class AdminModule extends AbstractWebModule implements Configurable<AdminConfig> {
    private final Console console = new Console();

    @Override
    protected void configure() {
        bind(Console.class).toInstance(console);
        message("conf/messages/admin");

        AdminConfig adminConfig = module(AdminModule.class);
        adminConfig.install(scriptModule());
        adminConfig.install(dashboardBundle());
        adminConfig.controller(AdminController.class);
        adminConfig.controller(AdminStaticResourceController.class);
        adminConfig.controller(RoleAdminController.class);
        adminConfig.controller(BundleMessageAdminController.class);
        adminConfig.controller(SwitchLanguageAdminController.class);
        onStartup(this::start);
    }


    private void start() {
        App app = app();
        Console console = require(Console.class);
        console.bundles().forEach(scriptModule -> {
            for (String path : scriptModule.messages) {
                app.message().bind(path, new ResourceMessageBundle(path, app.resource(), app.language()));
            }
        });
    }

    @Override
    public List<String> declareRoles() {
        ArrayList<String> declareRoles = Lists.newArrayList(super.declareRoles());
        declareRoles.add("*");
        return declareRoles;
    }

    @Override
    public AdminConfig configurator(AbstractModule module, Binder binder) {
        return new AdminConfigImpl(app().config(module, WebModule.class), console);
    }

    private ConsoleBundle scriptModule() {
        ConsoleBundle consoleBundle = new ConsoleBundle();
        consoleBundle.name = "adminBundle";
        consoleBundle.path = "/admin/runtime/";
        consoleBundle.scriptFile = "admin/static/app/app.min.js";
        consoleBundle.messages = Lists.newArrayList("conf/messages/element-ui", "conf/messages/admin");
        HashMap<String, Object> options = Maps.newHashMap();
        options.put("languages", languages());
        options.put("defaultLanguage", defaultLanguage());
        consoleBundle.options = options;
        return consoleBundle;
    }

    private ConsoleBundle dashboardBundle() {
        ConsoleMenu menu = new ConsoleMenu();
        menu.displayOrder = 0;
        menu.displayName = "dashboard.menu";
        menu.path = "/admin/dashboard";

        ConsoleMenu.Item item = new ConsoleMenu.Item();
        item.displayName = "dashboard.report";
        item.path = "/admin/dashboard/report";
        menu.children = Lists.newArrayList(item);

        ConsoleBundle consoleBundle = new ConsoleBundle();
        consoleBundle.name = "dashboardBundle";
        consoleBundle.path = "/admin/dashboard/";
        consoleBundle.scriptFile = "admin/static/dashboard/dashboard.min.js";
        consoleBundle.messages = Lists.newArrayList("conf/messages/dashboard");
        consoleBundle.menu = menu;
        return consoleBundle;
    }

    private List<LanguageView> languages() {
        MessageBundle messageManager = app().message();

        return app().supportLanguages().stream().map(language -> {
            LanguageView languageView = new LanguageView();
            languageView.displayName = messageManager.get("language." + language, language).orElse(language);
            languageView.value = language;
            return languageView;
        }).collect(Collectors.toList());
    }

    private LanguageView defaultLanguage() {
        MessageBundle messageManager = app().message();
        LanguageView languageView = new LanguageView();
        String language = app().language();
        languageView.displayName = messageManager.get("language." + language, app().language()).orElse(language);
        languageView.value = language;
        return languageView;
    }

    public static class LanguageView {
        public String displayName;
        public String value;
    }
}
