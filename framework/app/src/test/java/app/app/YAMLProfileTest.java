package app.app;

import app.app.impl.AppOptions;
import app.resource.Resource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author chi
 */
public class YAMLProfileTest {
    @Test
    public void options() {
        YAMLProfile profile = new YAMLProfile(Resource.classpath("conf/app.yml"));
        AppOptions options = profile.options("app", AppOptions.class);
        assertEquals("test", options.name);
        assertEquals("en", options.language);
    }

    @Test
    public void toYAML() {
        YAMLProfile profile = new YAMLProfile(Resource.classpath("conf/app.yml"));
        AppOptions options = profile.options("app", AppOptions.class);
        options.language = "zh";
        profile.setOptions("app", options);
        String yaml = profile.toYAML();
        assertTrue(yaml.contains("zh"));
    }
}