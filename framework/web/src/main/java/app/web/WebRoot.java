package app.web;


import app.resource.CompositeResourceRepository;
import app.resource.ResourceRepository;

/**
 * @author chi
 */
public class WebRoot extends CompositeResourceRepository {
    public WebRoot(ResourceRepository... repositories) {
        super(repositories);
    }
}
