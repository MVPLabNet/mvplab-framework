package app.swagger.service.integration;

import app.swagger.service.OpenAPISpecFilterImpl;
import app.swagger.service.Reader;
import com.google.common.collect.Sets;
import io.swagger.v3.oas.integration.ClasspathOpenApiConfigurationLoader;
import io.swagger.v3.oas.integration.FileOpenApiConfigurationLoader;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class ServletOpenApiConfigurationLoader implements OpenApiConfigurationLoader {

    private static Logger LOGGER = LoggerFactory.getLogger(ServletOpenApiConfigurationLoader.class);


    private FileOpenApiConfigurationLoader fileOpenApiConfigurationLoader = new FileOpenApiConfigurationLoader();
    private ClasspathOpenApiConfigurationLoader classpathOpenApiConfigurationLoader = new ClasspathOpenApiConfigurationLoader();


    @Override
    public OpenAPIConfiguration load(String path) throws IOException {
        SwaggerConfiguration configuration = new SwaggerConfiguration()
            .resourcePackages(Sets.newHashSet("app"))
            .filterClass(OpenAPISpecFilterImpl.class.getCanonicalName())
            .resourceClasses(Sets.newHashSet())
            .readAllResources(true)
            .prettyPrint(true)
            .sortOutput(true)
            .alwaysResolveAppPath(true)
            .readerClass(Reader.class.getCanonicalName())
            .cacheTTL(1000L)
            .scannerClass(JaxrsApplicationScanner.class.getCanonicalName());
//                .objectMapperProcessorClass(getInitParam(servletConfig, OPENAPI_CONFIGURATION_OBJECT_MAPPER_PROCESSOR_KEY))
//                .modelConverterClasses(resolveModelConverterClasses(servletConfig));

        return configuration;
    }

    @Override
    public boolean exists(String path) {

//        if (servletConfig == null) {
//            return false;
//        }
//        if (StringUtils.isBlank(path)) {
//            if (resolveResourcePackages(servletConfig) != null) {
//                return true;
//            }
//            if (getInitParam(servletConfig, OPENAPI_CONFIGURATION_FILTER_KEY) != null) {
//                return true;
//            }
//            if (resolveResourceClasses(servletConfig) != null) {
//                return true;
//            }
//            if (getBooleanInitParam(servletConfig, OPENAPI_CONFIGURATION_READALLRESOURCES_KEY) != null) {
//                return true;
//            }
//            if (getBooleanInitParam(servletConfig, OPENAPI_CONFIGURATION_PRETTYPRINT_KEY) != null) {
//                return true;
//            }
//            if (getBooleanInitParam(servletConfig, OPENAPI_CONFIGURATION_SORTOUTPUT_KEY) != null) {
//                return true;
//            }
//            if (getBooleanInitParam(servletConfig, OPENAPI_CONFIGURATION_ALWAYSRESOLVEAPPPATH_KEY) != null) {
//                return true;
//            }
//            if (getInitParam(servletConfig, OPENAPI_CONFIGURATION_READER_KEY) != null) {
//                return true;
//            }
//            if (getLongInitParam(servletConfig, OPENAPI_CONFIGURATION_CACHE_TTL_KEY) != null) {
//                return true;
//            }
//            if (getInitParam(servletConfig, OPENAPI_CONFIGURATION_SCANNER_KEY) != null) {
//                return true;
//            }
//            if (getInitParam(servletConfig, OPENAPI_CONFIGURATION_OBJECT_MAPPER_PROCESSOR_KEY) != null) {
//                return true;
//            }
//            return resolveModelConverterClasses(servletConfig) != null;
//        }
//        String location = ServletConfigContextUtils.getInitParam(servletConfig, path);
//        if (!StringUtils.isBlank(location)) {
//            if (classpathOpenApiConfigurationLoader.exists(location)) {
//                return true;
//            }
//            return fileOpenApiConfigurationLoader.exists(location);
//        }
        return true;
    }
}
