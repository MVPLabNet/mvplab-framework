package app.app;

import app.Profile;
import app.resource.ClasspathResourceRepository;
import app.resource.CompositeResourceRepository;
import app.resource.FileResourceRepository;
import app.resource.Resource;
import app.resource.ResourceRepository;
import app.util.JSON;
import app.util.YAML;
import app.util.exception.Errors;
import app.util.type.ClassValidator;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.glassfish.hk2.utilities.general.internal.MessageInterpolatorImpl;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.Period;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class YAMLProfile implements Profile {
    private final List<Class<?>> allowedTypes = Lists.newArrayList(Map.class, List.class, Locale.class, String.class, Integer.class, Long.class,
        Double.class, Boolean.class, Duration.class, LocalDate.class, OffsetDateTime.class, LocalTime.class, Period.class, Charset.class);
    private final Map<String, Map<String, Object>> options;
    private final Validator validator;

    public YAMLProfile(Resource resource) {
        options = YAML.fromYAML(resource.toText(Charsets.UTF_8), Map.class);
        validator = Validation.byDefaultProvider().configure()
            .messageInterpolator(new MessageInterpolatorImpl())
            .addProperty("hibernate.validator.fail_fast", "true").buildValidatorFactory().getValidator();
    }

    public static Profile load(Path dir) {
        ResourceRepository repository = new CompositeResourceRepository(new ClasspathResourceRepository(""), new FileResourceRepository(dir));
        Optional<Resource> resource = repository.get("conf/app.yml");
        if (!resource.isPresent()) {
            throw Errors.internalError("missing configuration file, path=conf/app.yml");
        }
        return new YAMLProfile(resource.get());
    }

    @Override
    public <T> T options(String name, Class<T> optionClass) {
        classValidator(optionClass).validate();
        Map<String, Object> options = Maps.newHashMap();
        options.putAll(defaultOptions(optionClass));
        if (this.options.containsKey(name)) {
            options.putAll(this.options.get(name));
        }
        T value = JSON.convert(options, optionClass);
        Set<ConstraintViolation<T>> violations = validator.validate(value);
        if (!violations.isEmpty()) {
            ConstraintViolation<T> violation = violations.iterator().next();
            throw Errors.internalError("invalid options, type={}, path={}, message={}", optionClass.getCanonicalName(), violation.getPropertyPath(), violation.getMessage());
        }
        return value;
    }

    public <T> void setOptions(String name, T options) {
        this.options.put(name, JSON.convert(options, Map.class));
    }

    @SuppressWarnings("unchecked")
    private <T> Map<String, Object> defaultOptions(Class<T> type) {
        try {
            return YAML.OBJECT_MAPPER.convertValue(type.getDeclaredConstructor().newInstance(), Map.class);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw Errors.internalError(e);
        }
    }

    public String toYAML() {
        return YAML.toYAML(options);
    }

    private ClassValidator classValidator(Class<?> optionClass) {
        ClassValidator classValidator = new ClassValidator(optionClass);
        classValidator.allowEnum();
        classValidator.allowGeneric();
        allowedTypes.forEach(classValidator::allow);
        return classValidator;
    }
}