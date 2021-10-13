package app.web.impl;

import app.resource.CompositeResourceRepository;
import app.resource.Resource;
import app.resource.ResourceRepository;

import java.util.Optional;

/**
 * @author chi
 */
public class ThemedResourceRepository implements ResourceRepository {
    private final String theme;
    private final ResourceRepository repository;

    public ThemedResourceRepository(String theme, ResourceRepository... repositories) {
        this.theme = theme;
        this.repository = new CompositeResourceRepository(repositories);
    }

    @Override
    public Optional<Resource> get(String path) {
        String themedPath = themedPath(path);
        Optional<Resource> resource = repository.get(themedPath);
        return resource.map(r -> new ThemedResource(path, r));
    }

    private String themedPath(String path) {
        return prefix() + path;
    }

    private String prefix() {
        return theme + '/';
    }
}
