package pro.documentum.persistence.common.query;

import org.datanucleus.query.expression.VariableExpression;

import pro.documentum.persistence.common.query.expression.DQLExpression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IVariableEvaluator {

    DQLExpression evaluate(VariableExpression expression,
            IDQLEvaluator evaluator);

}
