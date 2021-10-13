package app.codegen;

import java.util.List;

/**
 * @author chi
 */
public class MemberModel {
    public Boolean isId;
    public Boolean isIdGenerated;
    public Boolean isEnum;
    public String inputType;
    public String packageName;
    public String name;
    public String type;
    public Integer length = 255;
    public List<AnnotationModel> annotations;

    public Boolean getId() {
        return isId;
    }

    public Boolean getIdGenerated() {
        return isIdGenerated;
    }

    public Boolean getEnum() {
        return isEnum;
    }

    public String getInputType() {
        return inputType;
    }

    public Boolean getIsId() {
        return isId;
    }

    public Boolean getIsIdGenerated() {
        return isIdGenerated;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Integer getLength() {
        return length;
    }

    public List<AnnotationModel> getAnnotations() {
        return annotations;
    }
}
