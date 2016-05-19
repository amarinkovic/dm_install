package pro.documentum.jdo.query;

import org.datanucleus.query.expression.InvokeExpression;

import pro.documentum.jdo.query.expression.DQLExpression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IInvokeEvaluator {

    DQLExpression evaluate(InvokeExpression expression, IDQLEvaluator evaluator);

}
