package app.template;

/**
 * @author chi
 */
public interface Node {
    Element parent();

    void setParent(Element parent);

    String name();

    Integer row();

    Integer column();

    String source();

    default boolean isText() {
        return this instanceof Text;
    }

    default boolean isComment() {
        return this instanceof Comment;
    }

    default boolean isDocType() {
        return this instanceof DocType;
    }
}
