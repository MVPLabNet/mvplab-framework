package app.resource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;

/**
 * @author chi
 */
public class CompositeResourceRepository implements ResourceRepository {
    private final List<ResourceRepository> repositories;

    public CompositeResourceRepository(ResourceRepository... repositories) {
        this.repositories = Lists.newArrayList();
        for (ResourceRepository repository : repositories) {
            this.repositories.add(0, repository);
        }
    }

    public CompositeResourceRepository add(ResourceRepository repository) {
        repositories.add(0, repository);
        return this;
    }

    @Override
    public Optional<Resource> get(String path) {
        for (ResourceRepository repository : repositories) {
            Optional<Resource> source = repository.get(path);
            if (source.isPresent()) {
                return source;
            }
        }
        return Optional.empty();
    }

    public List<ResourceRepository> repositories() {
        return ImmutableList.copyOf(repositories);
    }
}
