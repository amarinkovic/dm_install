package pro.documentum.persistence.common.query.expression;

import java.util.List;

import org.datanucleus.query.expression.DyadicExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.VariableExpression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class DQLExpression {

    private final String _text;

    protected DQLExpression(final String text) {
        _text = text;
    }

    public static boolean isPrimary(final Expression expression) {
        return expression instanceof PrimaryExpression;
    }

    public static PrimaryExpression asPrimary(final Expression expression) {
        return (PrimaryExpression) expression;
    }

    public static boolean isInvoke(final Expression expression) {
        return expression instanceof InvokeExpression;
    }

    public static InvokeExpression asInvoke(final Expression expression) {
        return (InvokeExpression) expression;
    }

    public static boolean isLiteral(final Expression expression) {
        return expression instanceof Literal;
    }

    public static Literal asLiteral(final Expression expression) {
        return (Literal) expression;
    }

    public static boolean isParameter(final Expression expression) {
        return expression instanceof ParameterExpression;
    }

    public static ParameterExpression asParameter(final Expression expression) {
        return (ParameterExpression) expression;
    }

    public static boolean isVariable(final Expression expression) {
        return expression instanceof VariableExpression;
    }

    public static VariableExpression asVariable(final Expression expression) {
        return (VariableExpression) expression;
    }

    public static boolean isLiteralOrParameter(final Expression expression) {
        return isLiteral(expression) || isParameter(expression);
    }

    public static boolean isDyadic(final Expression expression) {
        return expression instanceof DyadicExpression;
    }

    public static boolean isDyadicNot(final Expression expression) {
        return isDyadic(expression)
                && expression.getOperator() == Expression.OP_NOT;
    }

    public static boolean hasRequiredArgs(final List<Expression> argExprs,
            final int num) {
        if (num > 0) {
            return argExprs != null && argExprs.size() == num;
        } else {
            return argExprs == null || argExprs.isEmpty();
        }
    }

    public String getText() {
        return _text;
    }

    public boolean isNull() {
        return false;
    }

}
