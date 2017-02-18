package pro.documentum.persistence.common.query.expression.functions;

import java.util.List;

import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.InvokeExpression;

import pro.documentum.persistence.common.query.IDQLEvaluator;
import pro.documentum.persistence.common.query.IInvokeEvaluator;
import pro.documentum.persistence.common.query.expression.DQLExpression;
import pro.documentum.persistence.common.query.expression.Expressions;
import pro.documentum.persistence.common.query.expression.literals.DQLString;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DQLDateToString extends DQLFunc {

    public static final String FUNC = "DATETOSTRING";

    private DQLDateToString(final String field, final String format) {
        super(String.format("%s(%s,'%s')", FUNC, field, format));
    }

    private static DQLDateToString evaluate(final InvokeExpression invokeExpr,
            final IDQLEvaluator evaluator) {
        String op = invokeExpr.getOperation();
        if (!FUNC.equalsIgnoreCase(op)) {
            return null;
        }
        List<Expression> dateExprs = invokeExpr.getArguments();
        if (!Expressions.hasRequiredArgs(dateExprs, 2)) {
            return null;
        }
        if (!Expressions.isPrimary(dateExprs.get(0))) {
            return null;
        }
        if (!Expressions.isLiteralOrParameter(dateExprs.get(1))) {
            return null;
        }
        DQLExpression field = (DQLExpression) evaluator
                .processPrimaryExpression(Expressions.asPrimary(dateExprs
                        .get(0)));
        if (field == null) {
            return null;
        }
        DQLString format = (DQLString) evaluator
                .processLiteralOrParameter(dateExprs.get(1));
        if (format == null) {
            return null;
        }
        return new DQLDateToString(field.getText(), format.getValue());
    }

    public static IInvokeEvaluator getInvokeEvaluator() {
        return new IInvokeEvaluator() {
            @Override
            public DQLExpression evaluate(final InvokeExpression expression,
                    final IDQLEvaluator evaluator) {
                return DQLDateToString.evaluate(expression, evaluator);
            }
        };
    }

}
