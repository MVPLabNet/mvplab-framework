package app.swagger.service.integration;

import app.swagger.service.integration.api.WebOpenApiContext;
import io.swagger.v3.oas.integration.api.OpenApiConfigurationLoader;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class XmlWebOpenApiContext<T extends XmlWebOpenApiContext<T>> extends JaxrsOpenApiContext<T> implements WebOpenApiContext {
    Logger LOGGER = LoggerFactory.getLogger(XmlWebOpenApiContext.class);


    @Override
    protected List<ImmutablePair<String, String>> getKnownLocations() {

        List<ImmutablePair<String, String>> locations = new LinkedList<>(Arrays.asList(
            new ImmutablePair<>("servlet", "/"),
            new ImmutablePair<>("servletpath", "openapi-configuration.yaml"),
            new ImmutablePair<>("servletpath", "openapi-configuration.json"),
            new ImmutablePair<>("servletpath", "WEB-INF/openapi-configuration.yaml"),
            new ImmutablePair<>("servletpath", "WEB-INF/openapi-configuration.json"),
            new ImmutablePair<>("servletpath", "openapi.yaml"),
            new ImmutablePair<>("servletpath", "openapi.json"),
            new ImmutablePair<>("servletpath", "WEB-INF/openapi.yaml"),
            new ImmutablePair<>("servletpath", "WEB-INF/openapi.json")
        ));
        locations.addAll(super.getKnownLocations());
        locations.add(new ImmutablePair<>("servlet", ""));  // get config from init params
        return locations;
    }

    @Override
    protected Map<String, OpenApiConfigurationLoader> getLocationLoaders() {
        Map<String, OpenApiConfigurationLoader> map = super.getLocationLoaders();
        map.put("servlet", new ServletOpenApiConfigurationLoader());
        return map;
    }

}
