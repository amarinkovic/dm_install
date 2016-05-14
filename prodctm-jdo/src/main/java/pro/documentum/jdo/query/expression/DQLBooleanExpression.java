package pro.documentum.jdo.query.expression;

import org.datanucleus.query.expression.Expression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLBooleanExpression extends DQLExpression {

    public DQLBooleanExpression(final String text) {
        super(text);
    }

    public static DQLBooleanExpression getInstance(
            final DQLFieldExpression fieldExpression, final DQLLiteral literal,
            final Expression.Operator op) {
        StringBuilder builder = new StringBuilder();
        if (fieldExpression.isRepeating()) {
            builder.append("ANY ");
        }
        builder.append(fieldExpression.getText());
        if (literal.isNull()) {
            // todo add NULLDATE and NULLSTRING
            if (op == Expression.OP_EQ) {
                builder.append(" IS NULL");
            } else if (op == Expression.OP_NOTEQ) {
                builder.append(" IS NOT NULL");
            } else {
                return null;
            }
        } else {
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
            builder.append(literal.getText());
        }
        return new DQLBooleanExpression(builder.toString());
    }

    public static DQLBooleanExpression getInstance(
            final DQLBooleanExpression left,
            final Expression.DyadicOperator op, final DQLBooleanExpression rigth) {
        if (op == Expression.OP_AND) {
            return new DQLBooleanExpression("(" + left.getText() + ") AND ("
                    + rigth.getText() + ")");
        }
        if (op == Expression.OP_OR) {
            return new DQLBooleanExpression("(" + left.getText() + ") OR ("
                    + rigth.getText() + ")");
        }
        return null;
    }

    public static DQLBooleanExpression getInstance(
            final DQLBooleanExpression expr, final Expression.MonadicOperator op) {
        if (op == Expression.OP_NOT) {
            return new DQLBooleanExpression(" NOT (" + expr.getText() + ")");
        }
        return null;
    }

}
