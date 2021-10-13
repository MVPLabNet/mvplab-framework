package app.web.impl;

import app.ApplicationException;
import app.resource.ClasspathResourceRepository;
import app.template.Template;
import app.template.TemplateEngine;
import app.web.impl.processor.HrefElementProcessor;
import app.web.impl.processor.SrcElementProcessor;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author chi
 */
class ThemedResourceRepositoryTest {
    TemplateEngine templateEngine;


    @BeforeEach
    public void setup() {
        templateEngine = new TemplateEngine();
        ClasspathResourceRepository repository = new ClasspathResourceRepository("web");
        templateEngine.addRepository(repository);
        templateEngine.addElementProcessor(new HrefElementProcessor(Lists.newArrayList(), repository, false));
        templateEngine.addElementProcessor(new SrcElementProcessor(Lists.newArrayList(), repository, false));
        templateEngine.addRepository(new ThemedResourceRepository("test", repository));
    }

    @Test
    void themedResources() throws IOException {
        Template template = templateEngine.template("template/test-theme.html").orElseThrow(() -> new ApplicationException("missing template"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        template.output(Maps.newHashMap(), out);
        assertEquals("<!doctype html><html lang=\"en-US\"><head><meta charset=\"UTF-8\"/><link rel=\"stylesheet\" href=\"/theme/test/static/css/css.css\" type=\"text/css\"/></head><body>themed template</body></html>", new String(out.toByteArray(), Charsets.UTF_8));
    }
}