package pro.documentum.persistence.common.query.expression.functions;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.expression.PrimaryExpression;

import pro.documentum.persistence.common.query.IDQLEvaluator;
import pro.documentum.persistence.common.query.expression.DQLExpression;
import pro.documentum.persistence.common.query.expression.Expressions;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLFunc extends DQLExpression {

    public DQLFunc(final String func) {
        super(func);
    }

    public static boolean isFuncExpression(final DQLExpression expression) {
        return expression instanceof DQLFunc;
    }

    protected static DQLExpression processField(
            final InvokeExpression invokeExpr,
            final IDQLEvaluator<?> evaluator, final String outer,
            final String right) {
        List<Expression> argExprs = invokeExpr.getArguments();
        PrimaryExpression primaryExpression = processFieldOuter(invokeExpr,
                outer, argExprs);
        if (primaryExpression == null) {
            primaryExpression = processFieldRight(invokeExpr, right, argExprs);
        }

        if (primaryExpression == null) {
            return null;
        }

        return (DQLExpression) evaluator
                .processPrimaryExpression(primaryExpression);
    }

    protected static DQLExpression processLiteralOrParameter(
            final InvokeExpression invokeExpr, final IDQLEvaluator<?> evaluator,
            final String outer, final String right) {
        List<Expression> argExprs = invokeExpr.getArguments();
        Expression literalOrParameter = processLiteralOrParameterOuter(
                invokeExpr, outer, argExprs);
        if (literalOrParameter == null) {
            literalOrParameter = processLiteralOrParameterRight(invokeExpr,
                    right, argExprs);
        }

        if (literalOrParameter == null) {
            return null;
        }

        return evaluator.processLiteralOrParameter(literalOrParameter);
    }

    protected static PrimaryExpression processFieldOuter(
            final InvokeExpression invokeExpr, final String outer,
            final List<Expression> argExprs) {
        String op = invokeExpr.getOperation();
        PrimaryExpression primaryExpression = null;
        if (StringUtils.isNotBlank(outer) && outer.equalsIgnoreCase(op)) {
            if (!Expressions.hasRequiredArgs(argExprs, 1)) {
                return null;
            }
            if (!Expressions.isPrimary(argExprs.get(0))) {
                return null;
            }
            primaryExpression = Expressions.asPrimary(argExprs.get(0));
        }
        return primaryExpression;
    }

    protected static Expression processLiteralOrParameterOuter(
            final InvokeExpression invokeExpr, final String outer,
            final List<Expression> argExprs) {
        String op = invokeExpr.getOperation();
        Expression result = null;
        if (StringUtils.isNotBlank(outer) && outer.equalsIgnoreCase(op)) {
            if (!Expressions.hasRequiredArgs(argExprs, 1)) {
                return null;
            }
            if (!Expressions.isLiteralOrParameter(argExprs.get(0))) {
                return null;
            }
            result = argExprs.get(0);
        }
        return result;
    }

    protected static PrimaryExpression processFieldRight(
            final InvokeExpression invokeExpr, final String right,
            final List<Expression> argExprs) {
        String op = invokeExpr.getOperation();
        PrimaryExpression primaryExpression = null;
        if (StringUtils.isNotBlank(right) && right.equalsIgnoreCase(op)) {
            if (!Expressions.hasRequiredArgs(argExprs, 0)) {
                return null;
            }
            if (!Expressions.isPrimary(invokeExpr.getLeft())) {
                return null;
            }
            primaryExpression = Expressions.asPrimary(invokeExpr.getLeft());
        }
        return primaryExpression;
    }

    protected static Expression processLiteralOrParameterRight(
            final InvokeExpression invokeExpr, final String right,
            final List<Expression> argExprs) {
        String op = invokeExpr.getOperation();
        Expression result = null;
        if (StringUtils.isNotBlank(right) && right.equalsIgnoreCase(op)) {
            if (!Expressions.hasRequiredArgs(argExprs, 0)) {
                return null;
            }
            if (!Expressions.isLiteralOrParameter(invokeExpr.getLeft())) {
                return null;
            }
            result = invokeExpr.getLeft();
        }
        return result;
    }

}
