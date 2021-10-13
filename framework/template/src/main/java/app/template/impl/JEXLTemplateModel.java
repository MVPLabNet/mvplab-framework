package app.template.impl;

import app.template.Expression;
import com.google.common.collect.Lists;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

import java.util.List;
import java.util.Map;

/**
 * @author chi
 */
public class JEXLTemplateModel implements TemplateModel {
    private final JexlEngine jexlEngine;
    private final List<JexlExpression> expressions = Lists.newArrayList();

    public JEXLTemplateModel(JexlEngine jexlEngine) {
        this.jexlEngine = jexlEngine;
    }

    @Override
    public Object eval(int expressionId, Map<String, Object> bindings) {
        return expressions.get(expressionId).evaluate(new MapContext(bindings));
    }

    @Override
    public Expression add(String expression, Expression defaultValue, Integer row, Integer col, String source) {
        JexlExpression e = jexlEngine.createExpression(expression);
        expressions.add(e);
        return new ExpressionImpl(expressions.size() - 1, expression, defaultValue, row, col, source, this);
    }
}
