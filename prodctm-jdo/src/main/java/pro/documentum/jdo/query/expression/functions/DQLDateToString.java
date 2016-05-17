package pro.documentum.jdo.query.expression.functions;

import java.util.List;

import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.InvokeExpression;

import pro.documentum.jdo.query.expression.DQLExpression;
import pro.documentum.jdo.query.expression.literals.DQLString;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DQLDateToString extends DQLFieldFunc {

    public static final String FUNC = "DATETOSTRING";

    private DQLDateToString(final String field, final String format) {
        super(String.format("%s(%s,'%s')", FUNC, field, format));
    }

    public static boolean isDateToString(final InvokeExpression invokeExpr) {
        String op = invokeExpr.getOperation();
        if (!FUNC.equalsIgnoreCase(op)) {
            return false;
        }
        List<Expression> dateExprs = invokeExpr.getArguments();
        if (dateExprs == null || dateExprs.isEmpty()) {
            return false;
        }
        if (dateExprs.size() != 2) {
            return false;
        }
        Expression fieldExpression = dateExprs.get(0);
        if (!isPrimary(fieldExpression)) {
            return false;
        }
        Expression formatExpression = dateExprs.get(1);
        return isLiteralOrParameter(formatExpression);
    }

    public static DQLDateToString getInstance(final DQLExpression field,
            final DQLExpression format) {
        if (!(format instanceof DQLString)) {
            return null;
        }
        return new DQLDateToString(field.getText(), ((DQLString) format)
                .getValue());
    }

}
