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
public class FormNumberCodeBuilder extends AbstractFormItemCodeBuilder {
    private final MemberModel memberModel;

    public FormNumberCodeBuilder(EntityModel entityModel, MemberModel memberModel, VelocityEngine templateEngine) {
        super(entityModel, "codegen/react/form/form.number.vm", templateEngine);
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
        context.put("member", numberFieldModel(memberModel));
        StringWriter writer = new StringWriter();
        template().merge(context, writer);
        return writer.toString();
    }

    private NumberFieldModel numberFieldModel(MemberModel memberModel) {
        NumberFieldModel numberFieldModel = new NumberFieldModel();
        numberFieldModel.name = memberModel.name;
        numberFieldModel.min = min(memberModel);
        numberFieldModel.max = max(memberModel);
        return numberFieldModel;
    }

    private String min(MemberModel memberModel) {
        for (AnnotationModel annotation : memberModel.annotations) {
            if (annotation.isConstraints && (annotation.type.equals("Min") || annotation.type.equals("DecimalMin"))) {
                Object value = annotation.values.get("value");
                return value == null ? null : value.toString();
            }
        }
        return null;
    }

    private String max(MemberModel memberModel) {
        for (AnnotationModel annotation : memberModel.annotations) {
            if (annotation.isConstraints && (annotation.type.equals("Max") || annotation.type.equals("DecimalMax"))) {
                Object value = annotation.values.get("value");
                return value == null ? null : value.toString();
            }
        }
        return null;
    }

    @Override
    public Path path() {
        throw new RuntimeException("not support");
    }

    public static class NumberFieldModel {
        public String name;
        public String min;
        public String max;

        public String getName() {
            return name;
        }

        public String getMin() {
            return min;
        }

        public String getMax() {
            return max;
        }
    }
}
