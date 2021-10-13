package app.template;

/**
 * @author chi
 */
public class Comment extends Element {
    private final String comment;

    public Comment(String comment, Integer row, Integer column, String source) {
        super("COMMENT", false, row, column, source);
        this.comment = comment;
    }

    public String outerHtml() {
        return comment;
    }
}
