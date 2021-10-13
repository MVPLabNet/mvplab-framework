package app.codegen.builder;

import app.codegen.AbstractCodeBuilder;
import app.codegen.EntityModel;
import org.apache.velocity.app.VelocityEngine;

import java.nio.file.Path;

/**
 * @author chi
 */
public abstract class AbstractReactCodeBuilder extends AbstractCodeBuilder {
    public AbstractReactCodeBuilder(EntityModel entityModel, String templatePath, VelocityEngine templateEngine) {
        super(entityModel, templatePath, templateEngine);
    }

    protected Path reactPath() {
        Path current = entityModel().path;
        while (current != null && !current.getFileName().toString().equals("java")) {
            current = current.getParent();
        }
        if (current == null) {
            return entityModel().path;
        }
        return current.getParent().resolve("web");
    }
}
