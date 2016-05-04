package pro.documentum.jdo.query.expression;

import org.datanucleus.query.expression.Expression;

import com.documentum.fc.common.DfUtil;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLBooleanExpression extends DQLExpression {

    public DQLBooleanExpression(final String propName, final Object value,
            final Expression.Operator op) {
        String stringValue = String.valueOf(value);
        if (value != null && value instanceof String) {
            stringValue = DfUtil.toQuotedString(stringValue);
        }
        if (op == Expression.OP_EQ) {
            setDqlText(propName + " = " + stringValue);
        } else if (op == Expression.OP_NOTEQ) {
            setDqlText(propName + " != " + stringValue);
        } else if (op == Expression.OP_GT) {
            setDqlText(propName + " > " + stringValue);
        } else if (op == Expression.OP_GTEQ) {
            setDqlText(propName + " >= " + stringValue);
        } else if (op == Expression.OP_LT) {
            setDqlText(propName + " < " + stringValue);
        } else if (op == Expression.OP_LTEQ) {
            setDqlText(propName + " <= " + stringValue);
        }
    }

    public DQLBooleanExpression(final DQLBooleanExpression left,
            final Expression.DyadicOperator op, final DQLBooleanExpression rigth) {
        if (op == Expression.OP_AND) {
            setDqlText("(" + left.getDqlText() + ") AND (" + rigth.getDqlText()
                    + ")");
        } else if (op == Expression.OP_OR) {
            setDqlText("(" + left.getDqlText() + ") OR (" + rigth.getDqlText()
                    + ")");
        }
    }

    public DQLBooleanExpression(final DQLBooleanExpression expr,
            final Expression.MonadicOperator op) {
        if (op == Expression.OP_NOT) {
            setDqlText(" NOT (" + expr.getDqlText() + ")");
        }
    }

}
