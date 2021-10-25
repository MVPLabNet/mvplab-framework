package app.swagger.service.integration;


import app.swagger.service.Reader;
import app.swagger.service.integration.api.JaxrsOpenApiScanner;
import io.swagger.v3.oas.integration.GenericOpenApiContext;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.integration.api.OpenApiReader;
import io.swagger.v3.oas.integration.api.OpenApiScanner;
import jakarta.ws.rs.core.Application;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JaxrsOpenApiContext<T extends JaxrsOpenApiContext> extends GenericOpenApiContext<JaxrsOpenApiContext> implements OpenApiContext {
    Logger LOGGER = LoggerFactory.getLogger(JaxrsOpenApiContext.class);

    private Application app;

    public T app(Application app) {
        this.app = app;
        return (T) this;
    }

    @Override
    protected OpenApiReader buildReader(OpenAPIConfiguration openApiConfiguration) throws Exception {
        OpenApiReader reader;
        if (StringUtils.isNotBlank(openApiConfiguration.getReaderClass())) {
            Class cls = getClass().getClassLoader().loadClass(openApiConfiguration.getReaderClass());
            reader = (OpenApiReader) cls.newInstance();
        } else {
            reader = new Reader();
        }
        if (reader instanceof Reader) {
            ((Reader) reader).setApplication(app);
        }
        reader.setConfiguration(openApiConfiguration);
        return reader;
    }

    @Override
    protected OpenApiScanner buildScanner(OpenAPIConfiguration openApiConfiguration) throws Exception {

        OpenApiScanner scanner;
        if (StringUtils.isNotBlank(openApiConfiguration.getScannerClass())) {
            Class cls = getClass().getClassLoader().loadClass(openApiConfiguration.getScannerClass());
            scanner = (OpenApiScanner) cls.newInstance();
        } else {
            scanner = new JaxrsApplicationAndAnnotationScanner();
        }
        scanner.setConfiguration(openApiConfiguration);
        if (scanner instanceof JaxrsOpenApiScanner) {
            ((JaxrsOpenApiScanner) scanner).setApplication(app);
        }
        return scanner;
    }
}
