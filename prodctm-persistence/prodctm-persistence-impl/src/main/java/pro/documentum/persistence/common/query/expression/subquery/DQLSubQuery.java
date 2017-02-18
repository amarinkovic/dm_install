package pro.documentum.persistence.common.query.expression.subquery;

import org.datanucleus.query.expression.VariableExpression;
import org.datanucleus.store.query.Query;

import pro.documentum.persistence.common.query.IDQLEvaluator;
import pro.documentum.persistence.common.query.IDocumentumQuery;
import pro.documentum.persistence.common.query.IVariableEvaluator;
import pro.documentum.persistence.common.query.expression.DQLExpression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DQLSubQuery extends DQLExpression {

    private DQLSubQuery(final String text) {
        super(text);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Query<?> & IDocumentumQuery<?>> DQLSubQuery evaluate(
            final VariableExpression expression,
            final IDQLEvaluator<?> evaluator) {
        T query = (T) evaluator.getSubquery(expression.getId());
        if (query == null) {
            return null;
        }
        query.compile();
        return new DQLSubQuery("(" + query.getNativeQuery() + ")");
    }

    public static IVariableEvaluator getVariableEvaluator() {
        return new IVariableEvaluator() {
            @Override
            public DQLExpression evaluate(final VariableExpression expression,
                    final IDQLEvaluator<?> evaluator) {
                return DQLSubQuery.evaluate(expression, evaluator);
            }
        };
    }

}
