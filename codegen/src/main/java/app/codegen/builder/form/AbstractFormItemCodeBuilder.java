package app.codegen.builder.form;

import app.codegen.AbstractCodeBuilder;
import app.codegen.EntityModel;
import org.apache.velocity.app.VelocityEngine;

import java.util.List;

/**
 * @author chi
 */
public abstract class AbstractFormItemCodeBuilder extends AbstractCodeBuilder {
    protected AbstractFormItemCodeBuilder(EntityModel entityModel, String templatePath, VelocityEngine templateEngine) {
        super(entityModel, templatePath, templateEngine);
    }

    public abstract List<String> uiComponents();
}
