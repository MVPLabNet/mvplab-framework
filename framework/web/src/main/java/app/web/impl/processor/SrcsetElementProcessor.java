package app.web.impl.processor;

import app.ApplicationException;
import app.resource.Resource;
import app.resource.ResourceRepository;
import app.template.Attribute;
import app.template.Element;
import app.template.ElementProcessor;
import app.web.impl.ThemedResource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * @author chi
 */
public class SrcsetElementProcessor implements ElementProcessor {
    private final ResourceRepository repository;

    public SrcsetElementProcessor(ResourceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Element process(Element element, Resource resource) {
        if (!isSourceNode(element)) {
            return element;
        }
        Optional<Attribute> attributeOptional = element.attribute("srcset");
        if (attributeOptional.isEmpty() || attributeOptional.get().isDynamic()) {
            return element;
        }
        Attribute attribute = attributeOptional.get();
        String srcset = attribute.value();

        final String[] groups = srcset.split(",");
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < groups.length; i++) {
            String group = groups[i].trim();
            final String[] parts = group.split(" ");
            if (parts.length == 2) {
                String src = parts[0];
                String rate = parts[1];

                if (isRelativeURL(src)) {
                    String path = normalize(resource, src);
                    Optional<Resource> resourceOptional = repository.get(path);
                    if (resourceOptional.isPresent()) {
                        if (b.length() > 0) {
                            b.append(',');
                        }
                        b.append('/').append(path).append(' ').append(rate);
                    } else {
                        if (b.length() > 0) {
                            b.append(',');
                        }
                        b.append(group);
                    }
                } else {
                    if (b.length() > 0) {
                        b.append(',');
                    }
                    b.append(group);
                }
            } else {
                if (b.length() > 0) {
                    b.append(',');
                }
                b.append(group);
            }
        }

        attribute.setValue(b.toString());
        return element;
    }

    private String normalize(Resource resource, String src) {
        try {
            if (resource instanceof ThemedResource) {
                ThemedResource themedResource = (ThemedResource) resource;
                return new URI(themedResource.raw().path()).resolve(src).normalize().getPath();
            }
            return new URI(resource.path()).resolve(src).normalize().getPath();
        } catch (URISyntaxException e) {
            throw new ApplicationException(e);
        }
    }

    private boolean isRelativeURL(String path) {
        return !(path.length() > 0 && path.charAt(0) == '/') && !path.startsWith("http://") && !path.startsWith("https://");
    }

    private boolean isSourceNode(Element node) {
        return "source".equalsIgnoreCase(node.name());
    }

}
