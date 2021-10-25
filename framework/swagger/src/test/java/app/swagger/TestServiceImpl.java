package app.swagger;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.Map;

/**
 * @author chi
 */
public class TestServiceImpl implements TestService {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Map<String, Integer> get() {
        return Collections.singletonMap("hashCode", hashCode());
    }

    @GET
    @Path("/exception")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public void throwException() throws Exception {
        throw new Exception("exception");
    }
}
