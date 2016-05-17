package pro.documentum.jdo.query.expression;

import org.datanucleus.query.expression.Expression;

import pro.documentum.jdo.query.expression.literals.DQLLiteral;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLBoolean extends DQLExpression {

    public DQLBoolean(final String text) {
        super(text);
    }

    public static DQLBoolean getInstance(final DQLExpression left,
            final DQLExpression right, final Expression.Operator op) {
        StringBuilder builder = new StringBuilder();
        if (append(builder, left) == null) {
            return null;
        }
        if (right.isNull()) {
            if (op == Expression.OP_EQ) {
                builder.append(" IS ").append(right.getText());
            } else if (op == Expression.OP_NOTEQ) {
                builder.append(" IS NOT ").append(right.getText());
            } else {
                return null;
            }
            return new DQLBoolean(builder.toString());
        } else {
            if (append(builder, op) == null) {
                return null;
            }
        }
        if (append(builder, right) == null) {
            return null;
        }
        return new DQLBoolean(builder.toString());
    }

    public static DQLBoolean getInstance(final DQLBoolean left,
            final DQLBoolean right, final Expression.DyadicOperator op) {
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
        return new DQLBoolean(builder.toString());
    }

    public static DQLBoolean getInstance(final DQLBoolean expr,
            final Expression.MonadicOperator op) {
        if (op == Expression.OP_NOT) {
            return new DQLBoolean(" NOT (" + expr.getText() + ")");
        }
        return null;
    }

    private static StringBuilder append(final StringBuilder builder,
            final DQLExpression expression) {
        if (expression instanceof DQLField) {
            DQLField fieldExpression = (DQLField) expression;
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
        return expression instanceof DQLBoolean;
    }

    public static DQLBoolean asBooleanExpression(final DQLExpression expression) {
        return (DQLBoolean) expression;
    }

}
