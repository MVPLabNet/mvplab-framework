package app.service.impl;

import app.App;
import app.Binder;
import app.service.ServiceConfig;
import app.util.exception.Errors;
import com.google.common.base.Strings;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

import javax.ws.rs.client.WebTarget;

/**
 * @author chi
 */
public class ServiceConfigImpl implements ServiceConfig {
    private final Binder binder;
    private final App app;
    private final JerseyClient client;

    public ServiceConfigImpl(Binder binder, JerseyClient client, App app) {
        this.binder = binder;
        this.app = app;
        this.client = client;
    }

    @Override
    public <T> ServiceConfig service(Class<T> serviceClass, String serviceURL) {
        if (!isValidServiceURL(serviceURL)) {
            throw Errors.internalError("invalid service entry URL, service={}, serviceURL=", serviceClass.getCanonicalName(), serviceURL);
        }
        WebTarget target = client.target(serviceURL);
        T service = WebResourceFactory.newResource(serviceClass, target);
        binder.bind(serviceClass).toInstance(service);
        return this;
    }

    private boolean isValidServiceURL(String serviceURL) {
        return !Strings.isNullOrEmpty(serviceURL) && (serviceURL.startsWith("https://") || serviceURL.startsWith("http://"));
    }
}
