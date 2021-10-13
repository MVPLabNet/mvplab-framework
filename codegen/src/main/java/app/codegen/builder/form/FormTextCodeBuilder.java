package app.codegen.builder.form;

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
public class FormTextCodeBuilder extends AbstractFormItemCodeBuilder {
    private final MemberModel memberModel;

    public FormTextCodeBuilder(EntityModel entityModel, MemberModel memberModel, VelocityEngine templateEngine) {
        super(entityModel, "codegen/react/form/form.text.vm", templateEngine);
        this.memberModel = memberModel;
    }

    public FormTextCodeBuilder(EntityModel entityModel, String templatePath, VelocityEngine templateEngine, MemberModel memberModel) {
        super(entityModel, templatePath, templateEngine);
        this.memberModel = memberModel;
    }

    @Override
    public List<String> uiComponents() {
        return Lists.newArrayList("Input");
    }

    @Override
    public String build() {
        VelocityContext context = new VelocityContext();
        EntityModel entityModel = entityModel();
        context.put("entity", entityModel);
        context.put("member", textMemberModel(memberModel));
        StringWriter writer = new StringWriter();
        template().merge(context, writer);
        return writer.toString();
    }

    @Override
    public Path path() {
        throw new RuntimeException("not support");
    }

    protected MemberModel member() {
        return memberModel;
    }

    private TextFieldModel textMemberModel(MemberModel memberModel) {
        TextFieldModel numberFieldModel = new TextFieldModel();
        numberFieldModel.name = memberModel.name;
        numberFieldModel.minLength = minLength(memberModel);
        numberFieldModel.maxLength = maxLength(memberModel);
        return numberFieldModel;
    }

    private String minLength(MemberModel memberModel) {
        for (AnnotationModel annotation : memberModel.annotations) {
            if (annotation.isConstraints && annotation.type.equals("Size")) {
                Object value = annotation.values.get("min");
                return value == null ? null : value.toString();
            }
        }
        return null;
    }

    private String maxLength(MemberModel memberModel) {
        for (AnnotationModel annotation : memberModel.annotations) {
            if (annotation.isConstraints && annotation.type.equals("Size")) {
                Object value = annotation.values.get("max");
                return value == null ? null : value.toString();
            }
        }
        return null;
    }

    public static class TextFieldModel {
        public String name;
        public String minLength;
        public String maxLength;

        public String getName() {
            return name;
        }

        public String getMinLength() {
            return minLength;
        }

        public String getMaxLength() {
            return maxLength;
        }
    }
}
