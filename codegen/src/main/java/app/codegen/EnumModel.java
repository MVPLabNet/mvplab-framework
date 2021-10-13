package app.codegen;

import java.util.List;

/**
 * @author chi
 */
public class EnumModel {
    public String name;
    public String type;
    public List<String> values;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List<String> getValues() {
        return values;
    }
}
