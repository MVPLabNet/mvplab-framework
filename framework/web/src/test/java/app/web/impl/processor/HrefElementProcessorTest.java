package app.web.impl.processor;

import app.resource.ClasspathResourceRepository;
import app.template.Template;
import app.template.TemplateEngine;
import app.template.TemplateResourceException;
import app.web.WebRoot;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author chi
 */
class HrefElementProcessorTest {
    @Test
    void process() throws IOException {
        TemplateEngine templateEngine = new TemplateEngine();
        WebRoot webRoot = new WebRoot();
        webRoot.add(new ClasspathResourceRepository("web"));
        templateEngine.addElementProcessor(new HrefElementProcessor(Lists.newArrayList("https://cdn.host/"), webRoot, false));
        templateEngine.addRepository(webRoot);

        Template template = templateEngine.template("template/href-element-processor.html").orElseThrow(() -> new TemplateResourceException("missing template"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        template.output(Maps.newHashMap(), out);
        assertEquals("<!doctype html><html lang=\"en-US\"><head><link rel=\"stylesheet\" href=\"https://cdn.host/static/css/css.css\" type=\"text/css\"/></head><body></body></html>", new String(out.toByteArray(), Charsets.UTF_8));
    }

    @Test
    void inline() throws IOException {
        TemplateEngine templateEngine = new TemplateEngine();
        WebRoot webRoot = new WebRoot();
        webRoot.add(new ClasspathResourceRepository("web"));
        templateEngine.addElementProcessor(new HrefElementProcessor(Lists.newArrayList("https://cdn.host/"), webRoot, true));
        templateEngine.addRepository(webRoot);

        Template template = templateEngine.template("template/href-element-processor.html").orElseThrow(() -> new TemplateResourceException("missing template"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        template.output(Maps.newHashMap(), out);
        assertEquals("<!doctype html><html lang=\"en-US\"><head><style>body {\n" +
            "    background: black;\n" +
            "}</style></head><body></body></html>", new String(out.toByteArray(), Charsets.UTF_8));
    }
}