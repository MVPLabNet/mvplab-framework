package app.codegen.builder;

import app.codegen.AnnotationModel;
import app.codegen.EntityModel;
import app.codegen.MemberModel;
import app.codegen.builder.form.FormDatePickerCodeBuilder;
import app.codegen.builder.form.FormNumberCodeBuilder;
import app.codegen.builder.form.FormSelectionCodeBuilder;
import app.codegen.builder.form.FormTextAreaCodeBuilder;
import app.codegen.builder.form.FormTextCodeBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * @author chi
 */
public class ReactUpdateCodeBuilder extends AbstractReactCodeBuilder {
    public ReactUpdateCodeBuilder(EntityModel entityModel, VelocityEngine templateEngine) {
        super(entityModel, "codegen/react/entity.update.vm", templateEngine);
    }

    @Override
    public String build() {
        VelocityContext context = new VelocityContext();
        EntityModel entityModel = entityModel();
        context.put("entity", entityModel);
        context.put("formMembers", formMembers(entityModel));
        context.put("formRules", formRules(entityModel));
        context.put("formItems", formItems(entityModel));
        context.put("uiComponents", uiComponents(entityModel));
        StringWriter writer = new StringWriter();
        template().merge(context, writer);
        return writer.toString();
    }

    public List<MemberModel> formMembers(EntityModel entityModel) {
        List<MemberModel> formMembers = Lists.newArrayList();
        for (MemberModel member : entityModel.members) {
            if (!isMemberExcluded(member)) {
                formMembers.add(member);
            }
        }
        return formMembers;
    }

    public List<String> uiComponents(EntityModel entityModel) {
        Set<String> uiComponents = Sets.newHashSet();
        for (MemberModel member : entityModel.members) {
            if (isMemberExcluded(member)) continue;
            switch (member.inputType) {
                case "text":
                    uiComponents.addAll(new FormTextCodeBuilder(entityModel, member, templateEngine()).uiComponents());
                    break;
                case "number":
                    uiComponents.addAll(new FormNumberCodeBuilder(entityModel, member, templateEngine()).uiComponents());
                    break;
                case "textarea":
                    uiComponents.addAll(new FormTextAreaCodeBuilder(entityModel, member, templateEngine()).uiComponents());
                    break;
                case "selection":
                    uiComponents.addAll(new FormSelectionCodeBuilder(entityModel, member, templateEngine()).uiComponents());
                    break;
                case "datepicker":
                    uiComponents.addAll(new FormDatePickerCodeBuilder(entityModel, member, templateEngine()).uiComponents());
                    break;
            }
        }
        return Lists.newArrayList(uiComponents);
    }

    public List<MemberValidationRule> formRules(EntityModel entityModel) {
        List<MemberValidationRule> rules = Lists.newArrayList();
        for (MemberModel member : entityModel.members) {
            if (isMemberExcluded(member)) continue;
            MemberValidationRule memberValidationRule = new MemberValidationRule();
            memberValidationRule.member = member;
            memberValidationRule.required = isRequired(member);
            memberValidationRule.type = type(member);

            if (memberValidationRule.required || memberValidationRule.type != null) {
                rules.add(memberValidationRule);
            }
        }
        return rules;
    }

    private List<FormItem> formItems(EntityModel entityModel) {
        List<FormItem> formItems = Lists.newArrayList();
        for (MemberModel member : entityModel.members) {
            if (isMemberExcluded(member)) continue;
            FormItem item = new FormItem();
            switch (member.inputType) {
                case "text":
                    item.html = new FormTextCodeBuilder(entityModel, member, templateEngine()).build();
                    break;
                case "number":
                    item.html = new FormNumberCodeBuilder(entityModel, member, templateEngine()).build();
                    break;
                case "textarea":
                    item.html = new FormTextAreaCodeBuilder(entityModel, member, templateEngine()).build();
                    break;
                case "selection":
                    item.html = new FormSelectionCodeBuilder(entityModel, member, templateEngine()).build();
                    break;
                case "datepicker":
                    item.html = new FormDatePickerCodeBuilder(entityModel, member, templateEngine()).build();
                    break;
            }
            formItems.add(item);
        }
        return formItems;
    }

    private boolean isMemberExcluded(MemberModel member) {
        if (member.name.equals("createdTime") || member.name.equals("createdBy") || member.name.equals("updatedTime") || member.name.equals("updatedBy")) {
            return true;
        }
        return false;
    }

    private boolean isRequired(MemberModel memberModel) {
        for (AnnotationModel annotation : memberModel.annotations) {
            if (annotation.type.equals("NotNull") || annotation.type.equals("NotEmpty") || annotation.type.equals("NotBlank")) {
                return true;
            }
        }
        return false;
    }

    private String type(MemberModel memberModel) {
        for (AnnotationModel annotation : memberModel.annotations) {
            if (annotation.type.equals("Email")) {
                return "email";
            }
            if (memberModel.type.equals("Date") || memberModel.type.equals("OffsetDateTime") || memberModel.type.equals("LocalDateTime")) {
                return "date";
            }
        }
        return null;
    }

    @Override
    public Path path() {
        EntityModel entityModel = entityModel();
        return reactPath().resolve("admin/" + entityModel.shortName + "/" + entityModel.shortName + ".update.jsx");
    }

    public static class MemberValidationRule {
        public MemberModel member;
        public Boolean required;
        public String type;

        public MemberModel getMember() {
            return member;
        }

        public Boolean getRequired() {
            return required;
        }

        public String getType() {
            return type;
        }
    }

    public static class FormItem {
        public String html;

        public String getHtml() {
            return html;
        }
    }
}
