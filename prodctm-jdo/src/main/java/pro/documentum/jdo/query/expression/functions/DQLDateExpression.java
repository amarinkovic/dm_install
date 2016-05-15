package pro.documentum.jdo.query.expression.functions;

import java.util.List;

import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.InvokeExpression;

import pro.documentum.jdo.query.expression.DQLExpression;
import pro.documentum.jdo.query.expression.literals.DQLDateLiteral;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLDateExpression extends DQLExpression {

    public DQLDateExpression(final String text) {
        super(text);
    }

    public static boolean isDate(final InvokeExpression invokeExpr) {
        String op = invokeExpr.getOperation();
        if (!"date".equalsIgnoreCase(op)) {
            return false;
        }

        List<Expression> dateExprs = invokeExpr.getArguments();
        if (dateExprs == null || dateExprs.isEmpty()) {
            return false;
        }
        if (dateExprs.size() > 2) {
            return false;
        }
        if (isSpecialDate(dateExprs)) {
            return true;
        }
        return isDateWithFormat(dateExprs);
    }

    private static boolean isDateWithFormat(final List<Expression> dateExprs) {
        if (dateExprs.size() != 2) {
            return false;
        }
        Expression valueExpr = dateExprs.get(0);
        Expression formatExpr = dateExprs.get(1);
        return isLiteralOrParameter(valueExpr)
                && isLiteralOrParameter(formatExpr);
    }

    private static boolean isSpecialDate(final List<Expression> dateExprs) {
        if (dateExprs.size() > 1) {
            return false;
        }
        Expression valueExpr = dateExprs.get(0);
        if (!isVariable(valueExpr)) {
            return false;
        }
        return DQLDateLiteral.isSpecialDateExpression(asVariable(valueExpr));
    }

}
