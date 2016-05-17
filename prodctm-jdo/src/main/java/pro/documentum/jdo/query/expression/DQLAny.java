package pro.documentum.jdo.query.expression;

import org.datanucleus.query.expression.InvokeExpression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLAny extends DQLBoolean {

    public static final String ANY = "ANY";

    public DQLAny(final String text) {
        super(String.format("%s (%s)", ANY, text));
    }

    public static boolean isAnyExpr(final InvokeExpression invokeExpr) {
        String op = invokeExpr.getOperation();
        return ANY.equalsIgnoreCase(op) && invokeExpr.getLeft() == null;
    }

}
