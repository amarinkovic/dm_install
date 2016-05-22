package pro.documentum.jdo.query.expression;

import org.datanucleus.query.expression.VariableExpression;

import pro.documentum.jdo.query.IDQLEvaluator;
import pro.documentum.jdo.query.IVariableEvaluator;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DQLSubQuery extends DQLExpression {

    private DQLSubQuery(final String text) {
        super(text);
    }

    private static DQLSubQuery evaluate(final VariableExpression expression,
            final IDQLEvaluator evaluator) {
        String name = expression.getId();
        if (!evaluator.hasSubQuery(name)) {
            return null;
        }
        return null;
    }

    public static IVariableEvaluator getVariableEvaluator() {
        return new IVariableEvaluator() {
            @Override
            public DQLExpression evaluate(final VariableExpression expression,
                    final IDQLEvaluator evaluator) {
                return DQLSubQuery.evaluate(expression, evaluator);
            }
        };
    }

}
