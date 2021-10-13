package app.util.type;


import org.slf4j.helpers.MessageFormatter;

/**
 * @author chi
 */
public class CodeBuilder {
    private final StringBuilder builder = new StringBuilder(256);

    public CodeBuilder prepend(String text) {
        builder.insert(0, text);
        return this;
    }

    public CodeBuilder prepend(String message, Object... params) {
        builder.insert(0, MessageFormatter.arrayFormat(message, params).getMessage());
        return this;
    }

    public CodeBuilder append(String text) {
        builder.append(text);
        return this;
    }

    public CodeBuilder append(String message, Object... params) {
        builder.append(MessageFormatter.arrayFormat(message, params).getMessage());
        return this;
    }

    public CodeBuilder indent(int indent) {
        for (int i = 0; i < indent; i++)
            builder.append("    ");
        return this;
    }

    public String build() {
        return builder.toString();
    }
}
