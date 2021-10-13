package app.template;

/**
 * @author chi
 */
public class DocType extends Element {
    private final String docType;

    public DocType(String docType, Integer row, Integer column, String source) {
        super("DOCTYPE", false, row, column, source);
        this.docType = docType;
    }

    public String outerHtml() {
        return docType;
    }
}
