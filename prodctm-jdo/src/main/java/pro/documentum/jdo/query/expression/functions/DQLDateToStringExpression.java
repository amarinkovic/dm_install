package pro.documentum.jdo.query.expression.functions;

import org.datanucleus.query.expression.InvokeExpression;

import pro.documentum.jdo.query.expression.DQLExpression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLDateToStringExpression extends DQLExpression {

    public DQLDateToStringExpression(final String text) {
        super(text);
    }

    public static boolean isDateToString(final InvokeExpression invokeExpr) {
        String op = invokeExpr.getOperation();
        return "datetostring".equalsIgnoreCase(op);
    }

}
