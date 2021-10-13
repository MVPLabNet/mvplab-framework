package app.util;

import app.ApplicationException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Locale;

/**
 * @author chi
 */
public final class YAML {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(new YAMLFactory().enable(YAMLGenerator.Feature.MINIMIZE_QUOTES));

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME, false);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
//        OBJECT_MAPPER.registerModule(new JaxbAnnotationModule());
        OBJECT_MAPPER.registerModule(new JSONModule());
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static <T> String toYAML(T instance) {
        try {
            return OBJECT_MAPPER.writeValueAsString(instance);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static <T> byte[] toYAMLBytes(T instance) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(instance);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static <T> T fromYAML(byte[] yaml, Type type) {
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructType(type);
            return OBJECT_MAPPER.readValue(yaml, javaType);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static <T> T fromYAML(String yaml, Type type) {
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructType(type);
            return OBJECT_MAPPER.readValue(yaml, javaType);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static class JSONModule extends SimpleModule {
        public JSONModule() {
            charset();
            locale();
        }

        private void locale() {
            addSerializer(Locale.class, new JsonSerializer<Locale>() {
                @Override
                public void serialize(Locale value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    if (value == null) {
                        gen.writeNull();
                    } else {
                        gen.writeString(value.toLanguageTag());
                    }
                }
            });

            addDeserializer(Locale.class, new JsonDeserializer<Locale>() {
                @Override
                public Locale deserialize(JsonParser p, DeserializationContext context) throws IOException {
                    String locale = p.getValueAsString();
                    if (locale == null) {
                        return null;
                    }
                    return Locale.forLanguageTag(locale);
                }
            });
        }

        private void charset() {
            addDeserializer(Charset.class, new JsonDeserializer<Charset>() {
                @Override
                public Charset deserialize(JsonParser p, DeserializationContext context) throws IOException {
                    String charset = p.getValueAsString();
                    if (charset == null) {
                        return null;
                    }
                    return Charset.forName(charset);
                }
            });

            addSerializer(Charset.class, new JsonSerializer<Charset>() {
                @Override
                public void serialize(Charset value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    if (value == null) {
                        gen.writeNull();
                    } else {
                        gen.writeString(value.name());
                    }
                }
            });
        }
    }
}
