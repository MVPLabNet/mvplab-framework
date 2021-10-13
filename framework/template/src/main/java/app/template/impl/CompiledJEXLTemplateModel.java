package app.template.impl;

import app.template.Expression;

import java.util.Map;

/**
 * TODO: compile JEXL AST
 *
 * @author chi
 */
public class CompiledJEXLTemplateModel implements TemplateModel {
    @Override
    public Object eval(int expressionId, Map<String, Object> bindings) {
        return null;
    }

    @Override
    public Expression add(String expression, Expression defaultValue, Integer row, Integer col, String source) {
        return null;
    }

}
