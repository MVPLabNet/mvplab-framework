package app.template;


/**
 * @author chi
 */
public class Attribute {
    private final String name;
    private String value;
    private final Boolean dynamic;
    private final Integer row;
    private final Integer column;
    private final String source;

    public Attribute(String name, Boolean dynamic, Integer row, Integer column, String source) {
        this.name = name;
        this.dynamic = dynamic;
        this.row = row;
        this.column = column;
        this.source = source;
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }

    public Boolean isDynamic() {
        return dynamic;
    }

    public Integer row() {
        return row;
    }

    public Integer column() {
        return column;
    }

    public String source() {
        return source;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
