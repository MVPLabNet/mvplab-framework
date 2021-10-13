package app.codegen.builder.form;

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
public class FormSelectionCodeBuilder extends AbstractFormItemCodeBuilder {
    private final MemberModel memberModel;

    public FormSelectionCodeBuilder(EntityModel entityModel, MemberModel memberModel, VelocityEngine templateEngine) {
        super(entityModel, "codegen/react/form/form.selection.vm", templateEngine);
        this.memberModel = memberModel;
    }

    @Override
    public List<String> uiComponents() {
        return Lists.newArrayList("Select");
    }

    @Override
    public String build() {
        VelocityContext context = new VelocityContext();
        EntityModel entityModel = entityModel();
        context.put("entity", entityModel);
        context.put("member", memberModel);
        StringWriter writer = new StringWriter();
        template().merge(context, writer);
        return writer.toString();
    }

    @Override
    public Path path() {
        throw new RuntimeException("not support");
    }
}
