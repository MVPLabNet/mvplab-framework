package app.swagger.service.integration.api;

import io.swagger.v3.oas.integration.api.OpenApiScanner;

import jakarta.ws.rs.core.Application;

public interface JaxrsOpenApiScanner extends OpenApiScanner {

    void setApplication(Application application);
}
