package app.codegen;

import java.util.Map;

/**
 * @author chi
 */
public class AnnotationModel {
    public String type;
    public String definition;
    public String packageName;
    public Boolean isConstraints;
    public Map<String, Object> values;

    public String getType() {
        return type;
    }

    public String getDefinition() {
        return definition;
    }

    public String getPackageName() {
        return packageName;
    }

    public Boolean getConstraints() {
        return isConstraints;
    }

    public Map<String, Object> getValues() {
        return values;
    }
}
