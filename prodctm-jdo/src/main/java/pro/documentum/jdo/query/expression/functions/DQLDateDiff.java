package pro.documentum.jdo.query.expression.functions;

import org.datanucleus.query.expression.InvokeExpression;

import pro.documentum.jdo.query.expression.DQLExpression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLDateDiff extends DQLExpression {

    public DQLDateDiff(final String text) {
        super(text);
    }

    public static boolean isDateDiff(final InvokeExpression invokeExpr) {
        String op = invokeExpr.getOperation();
        return "datetostring".equalsIgnoreCase(op);
    }

}
