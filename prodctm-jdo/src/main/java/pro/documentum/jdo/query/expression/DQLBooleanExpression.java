package pro.documentum.jdo.query.expression;

import org.datanucleus.query.expression.Expression;

import pro.documentum.jdo.query.expression.literals.DQLLiteral;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLBooleanExpression extends DQLExpression {

    public DQLBooleanExpression(final String text) {
        super(text);
    }

    public static DQLBooleanExpression getInstance(final DQLExpression left,
            final DQLExpression right, final Expression.Operator op) {
        StringBuilder builder = new StringBuilder();
        if (append(builder, left) == null) {
            return null;
        }
        if (right.isNull()) {
            if (op == Expression.OP_EQ) {
                builder.append(" IS NULL");
            } else if (op == Expression.OP_NOTEQ) {
                builder.append(" IS NOT NULL");
            } else {
                return null;
            }
            return new DQLBooleanExpression(builder.toString());
        } else {
            if (append(builder, op) == null) {
                return null;
            }
        }
        if (append(builder, right) == null) {
            return null;
        }
        return new DQLBooleanExpression(builder.toString());
    }

    public static DQLBooleanExpression getInstance(
            final DQLBooleanExpression left, final DQLBooleanExpression right,
            final Expression.DyadicOperator op) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(left.getText()).append(")");
        if (op == Expression.OP_AND) {
            builder.append(" AND ");
        } else if (op == Expression.OP_OR) {
            builder.append(" OR ");
        } else {
            return null;
        }
        builder.append("(").append(right.getText()).append(")");
        return new DQLBooleanExpression(builder.toString());
    }

    public static DQLBooleanExpression getInstance(
            final DQLBooleanExpression expr, final Expression.MonadicOperator op) {
        if (op == Expression.OP_NOT) {
            return new DQLBooleanExpression(" NOT (" + expr.getText() + ")");
        }
        return null;
    }

    private static StringBuilder append(final StringBuilder builder,
            final DQLExpression expression) {
        if (expression instanceof DQLFieldExpression) {
            DQLFieldExpression fieldExpression = (DQLFieldExpression) expression;
            if (fieldExpression.isRepeating()) {
                builder.append("ANY ");
            }
            builder.append(fieldExpression.getText());
        } else if (expression instanceof DQLLiteral) {
            DQLLiteral literal = (DQLLiteral) expression;
            builder.append(literal.getText());
        } else {
            return null;
        }
        return builder;
    }

    private static StringBuilder append(final StringBuilder builder,
            final Expression.Operator op) {
        if (op == Expression.OP_EQ) {
            builder.append("=");
        } else if (op == Expression.OP_NOTEQ) {
            builder.append("!=");
        } else if (op == Expression.OP_GT) {
            builder.append(">");
        } else if (op == Expression.OP_GTEQ) {
            builder.append(">=");
        } else if (op == Expression.OP_LT) {
            builder.append("<");
        } else if (op == Expression.OP_LTEQ) {
            builder.append("<=");
        } else {
            return null;
        }
        return builder;
    }

    public static boolean isBooleanExpression(final DQLExpression expression) {
        return expression instanceof DQLBooleanExpression;
    }

    public static DQLBooleanExpression asBooleanExpression(
            final DQLExpression expression) {
        return (DQLBooleanExpression) expression;
    }

}
