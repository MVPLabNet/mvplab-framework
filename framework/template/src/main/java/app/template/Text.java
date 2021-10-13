package app.template;

/**
 * @author chi
 */
public class Text implements Node {
    private final String text;
    private final Integer row;
    private final Integer column;
    private final String source;
    private Element parent;

    public Text(String text, Integer row, Integer column, String source) {
        this.text = text;
        this.row = row;
        this.column = column;
        this.source = source;
    }

    public String innerText() {
        return text;
    }

    @Override
    public Element parent() {
        return parent;
    }

    @Override
    public void setParent(Element parent) {
        this.parent = parent;
    }

    @Override
    public String name() {
        return "TEXT";
    }

    @Override
    public Integer row() {
        return row;
    }

    @Override
    public Integer column() {
        return column;
    }

    @Override
    public String source() {
        return source;
    }
}
