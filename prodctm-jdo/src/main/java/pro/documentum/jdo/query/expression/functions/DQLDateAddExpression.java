package pro.documentum.jdo.query.expression.functions;

import org.datanucleus.query.expression.InvokeExpression;

import pro.documentum.jdo.query.expression.DQLExpression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLDateAddExpression extends DQLExpression {

    public DQLDateAddExpression(final String text) {
        super(text);
    }

    public static boolean isDateAdd(final InvokeExpression invokeExpr) {
        String op = invokeExpr.getOperation();
        return "dateadd".equalsIgnoreCase(op);
    }

}
