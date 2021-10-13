package app.codegen.builder;

import app.codegen.AbstractCodeBuilder;
import app.codegen.EntityModel;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.nio.file.Path;

/**
 * @author chi
 */
public class DeleteRequestCodeBuilder extends AbstractCodeBuilder {
    public DeleteRequestCodeBuilder(EntityModel entityModel, VelocityEngine templateEngine) {
        super(entityModel, "codegen/java/web/api/entity/delete-request.vm", templateEngine);
    }

    @Override
    public String build() {
        VelocityContext context = new VelocityContext();
        EntityModel entityModel = entityModel();
        context.put("entity", entityModel);
        context.put("packageName", entityModel.basePackage + ".web.api." + entityModel.shortName);
        StringWriter writer = new StringWriter();
        template().merge(context, writer);

        ParserConfiguration configuration = new ParserConfiguration();
        JavaParser parser = new JavaParser(configuration);
        CompilationUnit compilationUnit = parser.parse(writer.toString()).getResult().orElseThrow();
        return compilationUnit.toString();
    }

    @Override
    public Path path() {
        EntityModel entityModel = entityModel();
        return entityModel.basePath.resolve("web/api/" + entityModel.shortName + "/Delete" + entityModel.type + "Request.java");
    }
}
