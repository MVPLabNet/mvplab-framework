package app.swagger.service.integration;

import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.OpenApiContextLocator;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import org.apache.commons.lang3.StringUtils;


public class ServletOpenApiContextBuilder<T extends ServletOpenApiContextBuilder> extends GenericOpenApiContextBuilder<ServletOpenApiContextBuilder> {
    @Override
    public OpenApiContext buildContext(boolean init) throws OpenApiConfigurationException {
        if (StringUtils.isBlank(ctxId)) {
            ctxId = OpenApiContext.OPENAPI_CONTEXT_ID_DEFAULT;
        }

        OpenApiContext ctx = OpenApiContextLocator.getInstance().getOpenApiContext(ctxId);

        if (ctx == null) {
            OpenApiContext rootCtx = OpenApiContextLocator.getInstance().getOpenApiContext(OpenApiContext.OPENAPI_CONTEXT_ID_DEFAULT);
            ctx = new XmlWebOpenApiContext()
//                    .servletConfig(servletConfig)
                .openApiConfiguration(openApiConfiguration)
                .id(ctxId)
                .parent(rootCtx);

            if (ctx.getConfigLocation() == null && configLocation != null) {
                ((XmlWebOpenApiContext) ctx).configLocation(configLocation);
            }
            if (((XmlWebOpenApiContext) ctx).getResourcePackages() == null && resourcePackages != null) {
                ((XmlWebOpenApiContext) ctx).resourcePackages(resourcePackages);
            }
            if (((XmlWebOpenApiContext) ctx).getResourceClasses() == null && resourceClasses != null) {
                ((XmlWebOpenApiContext) ctx).resourceClasses(resourceClasses);
            }
            if (init) {
                ctx.init(); // includes registering itself with OpenApiContextLocator
            }
        }
        return ctx;
    }
}
