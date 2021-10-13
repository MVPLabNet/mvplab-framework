package app.codegen.builder;

import app.codegen.AnnotationModel;
import app.codegen.AbstractCodeBuilder;
import app.codegen.EntityModel;
import app.codegen.MemberModel;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Set;

/**
 * @author chi
 */
public class WebControllerCodeBuilder extends AbstractCodeBuilder {
    public WebControllerCodeBuilder(EntityModel entityModel, VelocityEngine templateEngine) {
        super(entityModel, "codegen/java/web/web/web-controller.vm", templateEngine);
    }

    @Override
    public String build() {
        VelocityContext context = new VelocityContext();
        EntityModel entityModel = entityModel();
        context.put("entity", entityModel);
        context.put("packageName", entityModel.basePackage + ".web.web");
        Set<String> importRefs = Sets.newHashSet();
        for (MemberModel member : entityModel.members) {
            if (!Strings.isNullOrEmpty(member.packageName)) {
                importRefs.add(member.packageName + "." + member.type);
            }
            for (AnnotationModel annotation : member.annotations) {
                if (!Strings.isNullOrEmpty(annotation.packageName)) {
                    importRefs.add(annotation.packageName + "." + annotation.type);
                }
            }
        }
        importRefs.add(entityModel.packageName + "." + entityModel.type);
        context.put("importRefs", importRefs);
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
        return entityModel.basePath.resolve("web/web/" + entityModel.type + "WebController.java");
    }
}
