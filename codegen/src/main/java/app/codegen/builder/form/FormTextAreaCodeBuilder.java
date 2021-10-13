package app.codegen.builder.form;

import app.codegen.EntityModel;
import app.codegen.MemberModel;
import org.apache.velocity.app.VelocityEngine;

/**
 * @author chi
 */
public class FormTextAreaCodeBuilder extends FormTextCodeBuilder {
    public FormTextAreaCodeBuilder(EntityModel entityModel, MemberModel memberModel, VelocityEngine templateEngine) {
        super(entityModel, "codegen/react/form/form.textarea.vm", templateEngine, memberModel);
    }
}
