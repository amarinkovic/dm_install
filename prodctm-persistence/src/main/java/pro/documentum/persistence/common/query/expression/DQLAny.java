package pro.documentum.persistence.common.query.expression;

import java.util.List;

import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.InvokeExpression;

import pro.documentum.persistence.common.query.IDQLEvaluator;
import pro.documentum.persistence.common.query.IInvokeEvaluator;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLAny extends DQLBoolean {

    public static final String ANY = "ANY";

    public DQLAny(final String text) {
        super(String.format("%s (%s)", ANY, text));
    }

    private static DQLExpression evaluate(final InvokeExpression invokeExpr,
            final IDQLEvaluator evaluator) {
        String op = invokeExpr.getOperation();
        if (!ANY.equalsIgnoreCase(op)) {
            return null;
        }
        if (invokeExpr.getLeft() != null) {
            return null;
        }
        List<Expression> anyExprs = invokeExpr.getArguments();
        if (!Expressions.hasRequiredArgs(anyExprs, 1)) {
            return null;
        }
        Expression anyExpr = anyExprs.get(0);
        if (anyExpr.evaluate(evaluator) == null) {
            return null;
        }
        DQLExpression expression = evaluator.popExpression();
        if (DQLField.isFieldExpression(expression)) {
            expression = new DQLField(expression.getText(), true);
        } else if (DQLBoolean.isBooleanExpression(expression)) {
            expression = new DQLAny(expression.getText());
        } else {
            return null;
        }
        return expression;
    }

    public static IInvokeEvaluator getInvokeEvaluator() {
        return new IInvokeEvaluator() {
            @Override
            public DQLExpression evaluate(final InvokeExpression expression,
                    final IDQLEvaluator evaluator) {
                return DQLAny.evaluate(expression, evaluator);
            }
        };
    }

}
