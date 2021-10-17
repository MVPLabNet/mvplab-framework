package app.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author chi
 */
public class ContentTypeFilter implements ClientRequestFilter {
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        String contentType = requestContext.getHeaderString("Content-Type");
        if (Strings.isNullOrEmpty(contentType) || contentType.equalsIgnoreCase("application/octet-stream") && !(requestContext.getEntity() instanceof InputStream)) {
            requestContext.getHeaders().replace("Content-Type", ImmutableList.of("application/json"));
        }
    }
}
