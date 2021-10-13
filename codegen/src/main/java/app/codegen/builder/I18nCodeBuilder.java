package app.codegen.builder;

import app.codegen.AbstractCodeBuilder;
import app.codegen.AnnotationModel;
import app.codegen.EntityModel;
import app.codegen.MemberModel;
import com.google.common.collect.Lists;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.nio.file.Path;
import java.util.List;

/**
 * @author chi
 */
public class I18nCodeBuilder extends AbstractCodeBuilder {
    public I18nCodeBuilder(EntityModel entityModel, VelocityEngine templateEngine) {
        super(entityModel, "codegen/resources/messages/message.vm", templateEngine);
    }

    @Override
    public String build() {
        VelocityContext context = new VelocityContext();
        EntityModel entityModel = entityModel();
        context.put("entity", entityModel);
        List<MemberModel> validateMembers = Lists.newArrayList();
        for (MemberModel member : entityModel.members) {
            if (isValidateMember(member)) {
                validateMembers.add(member);
            }
        }
        context.put("validateMembers", validateMembers);
        StringWriter writer = new StringWriter();
        template().merge(context, writer);
        return writer.toString();
    }

    private boolean isValidateMember(MemberModel member) {
        for (AnnotationModel annotation : member.annotations) {
            if (annotation.isConstraints) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Path path() {
        return resourcePath().resolve("messages/" + entityModel().shortName + "_en_US.properties");
    }

    protected Path resourcePath() {
        Path current = entityModel().path;
        while (current != null && !current.getFileName().toString().equals("java")) {
            current = current.getParent();
        }
        if (current == null) {
            return entityModel().path;
        }
        return current.getParent().resolve("resources");
    }
}
