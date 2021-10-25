package app.swagger.service;

import app.swagger.SwaggerOptions;
import io.swagger.v3.core.filter.AbstractSpecFilter;
import io.swagger.v3.core.model.ApiDescription;
import io.swagger.v3.oas.models.PathItem;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author chi
 */
public class OpenAPISpecFilterImpl extends AbstractSpecFilter {
    SwaggerOptions swaggerOptions;

    public void setSwaggerOptions(SwaggerOptions options) {
        this.swaggerOptions = options;
    }

    @Override
    public Optional<PathItem> filterPathItem(PathItem pathItem, ApiDescription api, Map<String, List<String>> params, Map<String, String> cookies, Map<String, List<String>> headers) {
        if (Boolean.TRUE.equals(swaggerOptions.apiEnabled) && api.getPath().startsWith("/api/")) {
            return super.filterPathItem(pathItem, api, params, cookies, headers);
        }
        if (Boolean.TRUE.equals(swaggerOptions.webAPIEnabled) && api.getPath().startsWith("/web/api/")) {
            return super.filterPathItem(pathItem, api, params, cookies, headers);
        }
        if (Boolean.TRUE.equals(swaggerOptions.adminAPIEnabled) && api.getPath().startsWith("/admin/api/")) {
            return super.filterPathItem(pathItem, api, params, cookies, headers);
        }
        return Optional.empty();
    }
}
