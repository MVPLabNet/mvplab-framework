package app.codegen.builder;

import app.codegen.EntityModel;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.nio.file.Path;

/**
 * @author chi
 */
public class ReactViewCodeBuilder extends AbstractReactCodeBuilder {
    public ReactViewCodeBuilder(EntityModel entityModel, VelocityEngine templateEngine) {
        super(entityModel, "codegen/react/entity.view.vm", templateEngine);
    }

    @Override
    public String build() {
        VelocityContext context = new VelocityContext();
        EntityModel entityModel = entityModel();
        context.put("entity", entityModel);
        StringWriter writer = new StringWriter();
        template().merge(context, writer);
        return writer.toString();
    }

    @Override
    public Path path() {
        EntityModel entityModel = entityModel();
        return reactPath().resolve("admin/" + entityModel.shortName + "/" + entityModel.shortName + ".view.jsx");
    }
}
