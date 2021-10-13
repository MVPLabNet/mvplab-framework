package app.template.impl;

import app.template.Expression;
import app.template.TemplateException;

import java.util.Map;

/**
 * @author chi
 */
public class ExpressionImpl implements Expression {
    private final int id;
    private final String content;
    private final Integer row;
    private final Integer column;
    private final String source;
    private final Expression defaultValue;
    private final TemplateModel model;

    public ExpressionImpl(int id, String content, Expression defaultValue, Integer row, Integer column, String source, TemplateModel model) {
        this.id = id;
        this.content = content;
        this.defaultValue = defaultValue;
        this.row = row;
        this.column = column;
        this.source = source;
        this.model = model;
    }

    @Override
    public Object eval(Map<String, Object> bindings) {
        try {
            Object value = model.eval(id, bindings);
            if (value == null && defaultValue != null) {
                return defaultValue.eval(bindings);
            }
            return value;
        } catch (Exception e) {
            throw new TemplateException("failed to eval expression, expr={}, template={}, row={}, column={}", content, source, row, column, e);
        }
    }
}
