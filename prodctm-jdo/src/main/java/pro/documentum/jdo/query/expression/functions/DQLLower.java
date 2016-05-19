package pro.documentum.jdo.query.expression.functions;

import org.datanucleus.query.expression.InvokeExpression;

import pro.documentum.jdo.query.IDQLEvaluator;
import pro.documentum.jdo.query.IInvokeEvaluator;
import pro.documentum.jdo.query.expression.DQLExpression;
import pro.documentum.jdo.query.expression.DQLField;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DQLLower extends DQLFieldFunc {

    public static final String FUNC = "LOWER";

    public static final String RIGHT_FUNC = "toLowerCase";

    private DQLLower(final String field) {
        super(String.format("%s(%s)", FUNC, field));
    }

    private static DQLExpression evaluate(final InvokeExpression invokeExpr,
            final IDQLEvaluator evaluator) {
        DQLField field = process(invokeExpr, evaluator, FUNC, RIGHT_FUNC);
        if (field == null) {
            return null;
        }
        return new DQLLower(field.getText());
    }

    public static IInvokeEvaluator getInvokeEvaluator() {
        return new IInvokeEvaluator() {
            @Override
            public DQLExpression evaluate(final InvokeExpression expression,
                    final IDQLEvaluator evaluator) {
                return DQLLower.evaluate(expression, evaluator);
            }
        };
    }

}
