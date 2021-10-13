package app.template.impl;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author chi
 */
public class TemplateLiteral {
    private final String text;
    private final List<Token> tokens;

    public TemplateLiteral(String text) {
        this.text = text;
        tokens = parse(text);
    }

    private List<Token> parse(String text) {
        if (Strings.isNullOrEmpty(text)) {
            return ImmutableList.of();
        }
        List<Token> tokens = Lists.newArrayList();
        int state = 0;
        int p = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (state == 0 && c == '{' && i + 1 < text.length() && text.charAt(i + 1) == '{') {
                if (i > p) {
                    Token token = new Token();
                    token.text = text;
                    token.start = p;
                    token.end = i;
                    token.dynamic = false;
                    tokens.add(token);
                }
                state = 1;
                p = i + 2;
                i++;
            } else if (state == 1 && c == '}' && i + 1 < text.length() && text.charAt(i + 1) == '}') {
                if (i > p) {
                    Token token = new Token();
                    token.text = text;
                    token.start = p;
                    token.end = i;
                    token.dynamic = true;
                    tokens.add(token);
                }
                state = 0;
                p = i + 2;
            } else if (c == '\'' && (i == 0 || text.charAt(i - 1) != '\\')) {
                if (state == 0) {
                    state = state | 4;
                } else {
                    state = state ^ 4;
                }
            } else if (c == '\"' && (i == 0 || text.charAt(i - 1) != '\\')) {
                if (state == 0) {
                    state = state | 8;
                } else {
                    state = state ^ 8;
                }
            }
        }

        if (p < text.length()) {
            Token token = new Token();
            token.text = text;
            token.start = p;
            token.end = text.length();
            token.dynamic = false;
            tokens.add(token);
        }
        return tokens;
    }

    public boolean isDynamic() {
        for (Token token : tokens) {
            if (token.dynamic) {
                return true;
            }
        }
        return false;
    }

    public List<Token> tokens() {
        return tokens;
    }

    public String expr() {
        StringBuilder b = new StringBuilder();
        for (Token token : tokens) {
            if (b.length() > 0) {
                b.append('+');
            }
            if (token.dynamic) {
                b.append(text, token.start, token.end);
            } else {
                doubleQuote(token.start, token.end, b);
            }
        }
        return b.toString();
    }

    private String doubleQuote(int start, int end, StringBuilder b) {
        b.append("\"");
        for (int i = start; i < end; i++) {
            char c = text.charAt(i);
            if (c == '\"') {
                b.append("\\").append(c);
            } else if (Character.isWhitespace(c)) {
                if (!Character.isWhitespace(b.charAt(b.length() - 1))) {
                    b.append(' ');
                }
            } else {
                b.append(c);
            }
        }
        b.append("\"");
        return b.toString();
    }

    public static class Token {
        public String text;
        public int start;
        public int end;
        public boolean dynamic;

        public String content() {
            return text.substring(start, end);
        }
    }
}
