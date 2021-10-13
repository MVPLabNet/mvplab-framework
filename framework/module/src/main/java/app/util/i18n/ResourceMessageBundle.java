package app.util.i18n;

import app.ApplicationException;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import app.resource.Resource;
import app.resource.ResourceRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;


/**
 * @author chi
 */
public class ResourceMessageBundle implements MessageBundle {
    private final String path;
    private final ResourceRepository repository;
    private final String defaultLanguage;
    private final Map<String, Properties> messages = Maps.newConcurrentMap();

    public ResourceMessageBundle(String path, ResourceRepository repository, String defaultLanguage) {
        this.path = path;
        this.repository = repository;
        this.defaultLanguage = defaultLanguage;
    }

    @Override
    public Optional<String> get(String key) {
        return get(key, defaultLanguage);
    }

    @Override
    public Optional<String> get(String key, String language) {
        Properties properties = properties(language);
        if (properties.containsKey(key)) {
            String message = properties.getProperty(key);
            return Optional.of(message);
        } else {
            return Optional.empty();
        }
    }

    private Properties properties(String language) {
        Properties properties = messages.get(language);
        if (properties == null) {
            Optional<Resource> candidate = resource(language);
            if (!candidate.isPresent()) {
                candidate = resource(null);
            }
            Resource resource = candidate.orElseThrow(() -> new ApplicationException("missing message file, bundleFile={}", path));
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.openStream(), Charsets.UTF_8))) {
                properties = new Properties();
                properties.load(reader);
                messages.put(language, properties);
            } catch (IOException e) {
                throw new ApplicationException("failed to message bundle, file={}", path, e);
            }
        }
        return properties;
    }

    private Optional<Resource> resource(String language) {
        if (Strings.isNullOrEmpty(language)) {
            String path = this.path + ".properties";
            return repository.get(path);
        }

        Locale locale = Locale.forLanguageTag(language);
        if (locale.getCountry() != null && locale.getLanguage() != null) {
            String path = this.path + '_' + locale.getLanguage() + '_' + locale.getCountry() + ".properties";
            Optional<Resource> resource = repository.get(path);
            if (resource.isPresent()) {
                return resource;
            }
        }

        if (locale.getLanguage() != null) {
            String path = this.path + '_' + locale.getLanguage() + ".properties";
            Optional<Resource> resource = repository.get(path);
            if (resource.isPresent()) {
                return resource;
            }
        }

        return Optional.empty();
    }

    @Override
    public List<String> keys() {
        return properties(defaultLanguage).keySet().stream().map(Object::toString).collect(Collectors.toList());
    }
}
