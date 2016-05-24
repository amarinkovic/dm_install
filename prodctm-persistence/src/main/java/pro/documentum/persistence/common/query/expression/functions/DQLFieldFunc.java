package pro.documentum.persistence.common.query.expression.functions;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.expression.PrimaryExpression;

import pro.documentum.persistence.common.query.IDQLEvaluator;
import pro.documentum.persistence.common.query.expression.DQLField;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLFieldFunc extends DQLField {

    public DQLFieldFunc(final String func) {
        super(func);
    }

    protected static DQLField process(final InvokeExpression invokeExpr,
            final IDQLEvaluator evaluator, final String outer,
            final String right) {
        String op = invokeExpr.getOperation();
        List<Expression> argExprs = invokeExpr.getArguments();
        PrimaryExpression primaryExpression = null;
        if (StringUtils.isNotBlank(outer) && outer.equalsIgnoreCase(op)) {
            if (!hasRequiredArgs(argExprs, 1)) {
                return null;
            }
            if (!isPrimary(argExprs.get(0))) {
                return null;
            }
            primaryExpression = asPrimary(argExprs.get(0));
        }

        if (StringUtils.isNotBlank(right) && right.equalsIgnoreCase(op)) {
            if (!hasRequiredArgs(argExprs, 0)) {
                return null;
            }
            if (!isPrimary(invokeExpr.getLeft())) {
                return null;
            }
            primaryExpression = asPrimary(invokeExpr.getLeft());
        }

        if (primaryExpression == null) {
            return null;
        }

        return (DQLField) evaluator.processPrimaryExpression(primaryExpression);
    }

}
