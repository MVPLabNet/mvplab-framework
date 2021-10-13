package app.codegen;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;

import java.nio.file.Path;

/**
 * @author chi
 */
public abstract class AbstractCodeBuilder {
    private final EntityModel entityModel;
    private final String templatePath;
    private final VelocityEngine templateEngine;

    protected AbstractCodeBuilder(EntityModel entityModel, String templatePath, VelocityEngine templateEngine) {
        this.entityModel = entityModel;
        this.templatePath = templatePath;
        this.templateEngine = templateEngine;
    }

    public abstract String build();

    public abstract Path path();

    protected VelocityEngine templateEngine() {
        return templateEngine;
    }

    protected Template template() {
        return templateEngine.getTemplate(templatePath);
    }

    protected EntityModel entityModel() {
        return entityModel;
    }
}
