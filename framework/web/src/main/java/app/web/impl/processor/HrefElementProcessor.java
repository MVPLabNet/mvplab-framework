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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;

/**
 * @author chi
 */
public class HrefElementProcessor implements ElementProcessor {
    private final List<String> cdnBaseURLs;
    private final ResourceRepository repository;
    private final boolean inlineEnabled;

    public HrefElementProcessor(List<String> cdnBaseURLs, ResourceRepository repository, boolean inlineEnabled) {
        this.cdnBaseURLs = cdnBaseURLs;
        this.repository = repository;
        this.inlineEnabled = inlineEnabled;
    }

    @Override
    public Element process(Element element, Resource resource) {
        if (!isNormalizeRequired(element)) {
            return element;
        }
        Optional<Attribute> attributeOptional = element.attribute("href");
        if (attributeOptional.isEmpty() || attributeOptional.get().isDynamic()) {
            return element;
        }
        Attribute attribute = attributeOptional.get();
        String href = attribute.value();
        if (!isRelativeURL(href)) {
            return element;
        }
        String path = normalize(resource, href);
        Optional<Resource> targetResource = repository.get(path);
        if (targetResource.isEmpty()) {
            return element;

        }
        if (isCSSElement(element)) {
            if (inlineEnabled) {
                Element styleElement = new Element("targetResource", false, element.row(), element.column(), element.source());
                Text text = new Text(targetResource.get().toText(Charsets.UTF_8), element.row(), element.column(), element.source());
                styleElement.addChild(text);
                element.parent().replaceChild(element, styleElement);
            } else if (isCDNEnabled(resource)) {
                attribute.setValue(cdnURL(path));
            } else {
                attribute.setValue('/' + path);
            }
        } else {
            if (isCDNEnabled(resource)) {
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
            String encodeSrc = URLEncoder.encode(src, Charsets.UTF_8.name());
            if (resource instanceof ThemedResource) {
                ThemedResource themedResource = (ThemedResource) resource;
                return new URI(themedResource.raw().path()).resolve(encodeSrc).normalize().getPath();
            }
            return new URI(resource.path()).resolve(encodeSrc).normalize().getPath();
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            throw new ApplicationException(e);
        }
    }

    private boolean isRelativeURL(String path) {
        return !(path.length() > 0 && path.charAt(0) == '/') && !path.startsWith("http://") && !path.startsWith("https://") && !path.startsWith("://") && !path.contains("{{");
    }

    private boolean isCDNEnabled(Resource resource) {
        return cdnBaseURLs != null && !cdnBaseURLs.isEmpty() && resource.path().startsWith("template/");
    }

    private boolean isLinkElement(Element element) {
        return element.name().equalsIgnoreCase("link");
    }

    private boolean isCSSElement(Element element) {
        if (isLinkElement(element)) {
            Optional<Attribute> attribute = element.attribute("rel");
            return attribute.isPresent() && attribute.get().value().equals("stylesheet");
        }
        return false;
    }

    private boolean isNormalizeRequired(Element element) {
        return isCSSElement(element)
            || isIconElement(element)
            || isManifestElement(element);
    }

    private boolean isIconElement(Element element) {
        if (isLinkElement(element)) {
            Optional<Attribute> attribute = element.attribute("rel");
            return attribute.isPresent()
                && (attribute.get().value().equals("icon")
                || attribute.get().value().equals("apple-touch-icon"));
        }
        return false;
    }

    private boolean isManifestElement(Element element) {
        if (isLinkElement(element)) {
            Optional<Attribute> attribute = element.attribute("rel");
            return attribute.isPresent() && attribute.get().value().equals("manifest");
        }
        return false;
    }
}
