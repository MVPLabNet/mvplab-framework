package app.template.impl.segment;

import app.resource.SingleResourceRepository;
import app.resource.StringResource;
import app.template.Template;
import app.template.TemplateEngine;
import com.google.common.base.Charsets;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author chi
 */
public class ComponentAttributeExpressionTokenTest {
    @Test
    public void escape() throws IOException {
        TemplateEngine templateEngine = new TemplateEngine().addRepository(new SingleResourceRepository(new StringResource("/test.html", template())));
        Template template = templateEngine.template("/test.html").orElseThrow(RuntimeException::new);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        template.output(Collections.singletonMap("json", "{\"name\":\"value\"}"), out);
        assertEquals("<!doctype html><html lang=\"en-US\" xmlns:j=\"http://www.w3.org/1999/xhtml\"><head></head><body><div json2=\"json\" json=\"{\\\"name\\\":\\\"value\\\"}\"></div></body></html>", new String(out.toByteArray(), Charsets.UTF_8));
    }

    private String template() {
        return "<!doctype html>\n"
            + "<html lang=\"en-US\" xmlns:j=\"http://www.w3.org/1999/xhtml\">\n"
            + "<head>\n"
            + "</head>\n"
            + "<body>\n"
            + "<div j:json=\"{{json}}\" json2=\"json\"></div>\n"
            + "</body>\n"
            + "</html>";
    }
}