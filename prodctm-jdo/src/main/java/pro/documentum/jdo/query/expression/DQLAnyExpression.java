package pro.documentum.jdo.query.expression;

import org.datanucleus.query.expression.InvokeExpression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLAnyExpression extends DQLExpression {

    public DQLAnyExpression(final String text) {
        super("ANY (" + text + ")");
    }

    public static boolean isAnyExpr(final InvokeExpression invokeExpr) {
        String op = invokeExpr.getOperation();
        return "ANY".equalsIgnoreCase(op);
    }

}
