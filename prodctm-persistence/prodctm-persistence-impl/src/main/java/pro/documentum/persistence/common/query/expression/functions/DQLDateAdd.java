package pro.documentum.persistence.common.query.expression.functions;

import org.datanucleus.query.expression.InvokeExpression;

import pro.documentum.persistence.common.query.expression.DQLExpression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLDateAdd extends DQLExpression {

    public DQLDateAdd(final String text) {
        super(text);
    }

    public static boolean isDateAdd(final InvokeExpression invokeExpr) {
        String op = invokeExpr.getOperation();
        return "dateadd".equalsIgnoreCase(op);
    }

}
