package pro.documentum.jdo.query.expression.functions;

import org.datanucleus.query.expression.InvokeExpression;

import pro.documentum.jdo.query.expression.DQLExpression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLDateFloorExpression extends DQLExpression {

    public DQLDateFloorExpression(final String text) {
        super(text);
    }

    public static boolean isDateFloor(final InvokeExpression invokeExpr) {
        String op = invokeExpr.getOperation();
        return "datefloor".equalsIgnoreCase(op);
    }

}
