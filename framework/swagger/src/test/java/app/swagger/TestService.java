package app.swagger;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.util.Map;

/**
 * @author chi
 */
@Path("/api")
public interface TestService {
    @GET
    Map<String, Integer> get();

    @Path("/exception")
    @GET
    void throwException() throws Exception;
}
