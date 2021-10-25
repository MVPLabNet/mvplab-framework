package app.swagger;

import app.swagger.service.ModelResolver;
import app.swagger.web.OpenApiWebController;
import app.util.JSON;
import app.web.AbstractWebModule;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverters;

import java.util.List;

/**
 * @author chi
 */
public final class SwaggerModule extends AbstractWebModule {
    @Override
    protected void configure() {
        bind(SwaggerOptions.class).toInstance(app().profile().options("swagger", SwaggerOptions.class));
        bindController(OpenApiWebController.class);

        final List<ModelConverter> converters = ModelConverters.getInstance().getConverters();
        for (ModelConverter converter : converters) {
            ModelConverters.getInstance().removeConverter(converter);
        }
        ModelConverters.getInstance().addConverter(new ModelResolver(JSON.OBJECT_MAPPER));
    }
}
