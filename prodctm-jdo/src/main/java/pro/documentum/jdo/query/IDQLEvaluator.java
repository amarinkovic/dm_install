package pro.documentum.jdo.query;

import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.ExpressionEvaluator;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.store.query.Query;

import pro.documentum.jdo.query.expression.DQLExpression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IDQLEvaluator extends ExpressionEvaluator {

    DQLExpression popExpression();

    Object processPrimaryExpression(PrimaryExpression expr);

    DQLExpression processLiteralOrParameter(Expression expression);

    boolean hasSubQuery(String name);

    Query.SubqueryDefinition getSubQuery(String name);

}
