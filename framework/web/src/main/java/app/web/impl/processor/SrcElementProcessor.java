package app.web.impl.processor;

import app.ApplicationException;
import app.resource.Resource;
import app.resource.ResourceRepository;
import app.template.Attribute;
import app.template.Element;
import app.template.ElementProcessor;
import app.template.Text;
import app.web.impl.ThemedResource;
import com.google.common.base.Charsets;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * @author chi
 */
public class SrcElementProcessor implements ElementProcessor {
    private final List<String> cdnBaseURLs;
    private final ResourceRepository repository;
    private final boolean inlineEnabled;

    public SrcElementProcessor(List<String> cdnBaseURLs, ResourceRepository repository, boolean inlineEnabled) {
        this.cdnBaseURLs = cdnBaseURLs;
        this.repository = repository;
        this.inlineEnabled = inlineEnabled;
    }

    @Override
    public Element process(Element element, Resource resource) {
        if (!isScriptNode(element) && !isImageNode(element) && !isSourceNode(element)) {
            return element;
        }
        Optional<Attribute> attributeOptional = element.attribute("src");
        if (attributeOptional.isEmpty() || attributeOptional.get().isDynamic()) {
            return element;
        }
        Attribute attribute = attributeOptional.get();
        String src = attribute.value();
        if (!isRelativeURL(src)) {
            return element;
        }
        String path = normalize(resource, src);
        Optional<Resource> script = repository.get(path);
        if (script.isPresent()) {
            if (inlineEnabled && isScriptNode(element)) {
                element.deleteAttribute("src");
                Text text = new Text(script.get().toText(Charsets.UTF_8), element.row(), element.column(), element.source());
                element.addChild(text);
            } else if (isCDNEnabled(element, resource)) {
                attribute.setValue(cdnURL(path));
            } else {
                attribute.setValue('/' + path);
            }
        }
        return element;
    }

    private String cdnURL(String path) {
        String baseURL = cdnBaseURLs.get(Math.abs(path.hashCode() % cdnBaseURLs.size()));
        return baseURL + path;
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

    private boolean isScriptNode(Element node) {
        return "script".equalsIgnoreCase(node.name());
    }

    private boolean isImageNode(Element node) {
        return "img".equalsIgnoreCase(node.name());
    }

    private boolean isSourceNode(Element node) {
        return "source".equalsIgnoreCase(node.name());
    }

    private boolean isCDNEnabled(Element element, Resource resource) {
        return cdnBaseURLs != null && !cdnBaseURLs.isEmpty() && resource.path().startsWith("template/") && isScriptNode(element);
    }
}
