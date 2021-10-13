package app.resource;

import java.util.Optional;

/**
 * @author chi
 */
public class SingleResourceRepository implements ResourceRepository {
    private final Resource resource;

    public SingleResourceRepository(Resource resource) {
        this.resource = resource;
    }

    @Override
    public Optional<Resource> get(String path) {
        if (resource.path().equals(path)) {
            return Optional.of(resource);
        }
        return Optional.empty();
    }
}
