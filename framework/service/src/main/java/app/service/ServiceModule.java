package app.service;

import app.AbstractModule;
import app.Binder;
import app.Configurable;
import app.service.impl.ContentTypeFilter;
import app.service.impl.ServiceConfigImpl;
import app.util.JSON;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.validation.ValidationFeature;

/**
 * @author chi
 */
public final class ServiceModule extends AbstractModule implements Configurable<ServiceConfig> {
    private JerseyClient client;

    @Override
    protected void configure() {
        JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
        jacksonProvider.setMapper(JSON.OBJECT_MAPPER);

        client = new JerseyClientBuilder()
            .register(new ContentTypeFilter())
            .register(JacksonFeature.class)
            .register(ValidationFeature.class)
            .register(jacksonProvider)
            .build();
    }

    @Override
    public ServiceConfig configurator(AbstractModule module, Binder binder) {
        return new ServiceConfigImpl(binder, client, app());
    }
}
