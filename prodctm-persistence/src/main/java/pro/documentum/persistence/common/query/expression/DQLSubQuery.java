package pro.documentum.persistence.common.query.expression;

import org.datanucleus.query.expression.VariableExpression;

import pro.documentum.persistence.common.query.IDQLEvaluator;
import pro.documentum.persistence.common.query.IVariableEvaluator;

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
