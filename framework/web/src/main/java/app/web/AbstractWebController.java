package app.web;

import app.App;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * @author chi
 */
public abstract class AbstractWebController {
    @Inject
    protected App app;
    @Inject
    protected RequestInfo requestInfo;

    protected Response.ResponseBuilder template(String templatePath) {
        return template(templatePath, ImmutableMap.of());
    }

    protected Response.ResponseBuilder template(String templatePath, Map<String, Object> bindings) {
        Map<String, Object> templateBindings = Maps.newHashMapWithExpectedSize(bindings.size() + 3);
        templateBindings.putAll(bindings);
        templateBindings.put("app", appInfo(app));
        templateBindings.put("request", requestInfo);
        return Response.ok(new TemplateEntity(templatePath, templateBindings)).type(MediaType.TEXT_HTML);
    }

    private AppInfo appInfo(App app) {
        AppInfo appInfo = new AppInfo();
        appInfo.name = app.name();
        appInfo.baseURL = app.baseURL().endsWith("/") ? app.baseURL().substring(0, app.baseURL().length() - 1) : app.baseURL();
        appInfo.description = app.description();
        appInfo.imageURL = app.imageURL();
        appInfo.language = app.language();
        appInfo.supportLanguages = app.supportLanguages();
        return appInfo;
    }
}
