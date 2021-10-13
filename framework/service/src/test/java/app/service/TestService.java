package app.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
