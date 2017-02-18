package pro.documentum.persistence.common.query.expression.functions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.InvokeExpression;

import pro.documentum.persistence.common.query.IDQLEvaluator;
import pro.documentum.persistence.common.query.IInvokeEvaluator;
import pro.documentum.persistence.common.query.expression.DQLExpression;
import pro.documentum.persistence.common.query.expression.Expressions;
import pro.documentum.persistence.common.query.expression.literals.DQLString;
import pro.documentum.persistence.common.query.expression.literals.nulls.DQLNull;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLConstant extends DQLExpression {

    public static final String USER_CONST = "USER";

    public static final Set<String> CONSTANTS;

    static {
        CONSTANTS = new HashSet<>();
        CONSTANTS.add(USER_CONST);
    }

    public DQLConstant(final String text) {
        super(text);
    }

    private static DQLExpression evaluate(final InvokeExpression invokeExpr,
            final IDQLEvaluator evaluator) {
        List<Expression> argExprs = invokeExpr.getArguments();
        if (!Expressions.hasRequiredArgs(argExprs, 0)) {
            return null;
        }
        String op = invokeExpr.getOperation();
        if (DQLNull.isSpecialNull(op)) {
            return DQLNull.getInstance(op);
        }
        if (isConstant(op)) {
            return DQLString.getInstance(op.toUpperCase(), false);
        }
        return null;
    }

    public static IInvokeEvaluator getInvokeEvaluator() {
        return new IInvokeEvaluator() {
            @Override
            public DQLExpression evaluate(final InvokeExpression expression,
                    final IDQLEvaluator evaluator) {
                return DQLConstant.evaluate(expression, evaluator);
            }
        };
    }

    private static boolean isConstant(final String op) {
        if (op == null) {
            return false;
        }
        return CONSTANTS.contains(op.toUpperCase());
    }

}
