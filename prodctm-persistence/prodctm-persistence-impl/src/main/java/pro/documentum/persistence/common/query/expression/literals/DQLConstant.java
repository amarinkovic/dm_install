package pro.documentum.persistence.common.query.expression.literals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.expression.VariableExpression;

import pro.documentum.persistence.common.query.IDQLEvaluator;
import pro.documentum.persistence.common.query.IInvokeEvaluator;
import pro.documentum.persistence.common.query.IVariableEvaluator;
import pro.documentum.persistence.common.query.expression.DQLExpression;
import pro.documentum.persistence.common.query.expression.Expressions;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DQLConstant extends DQLLiteral<String> {

    public static final String USER_CONST = "USER";

    public static final Set<String> CONSTANTS;

    static {
        CONSTANTS = new HashSet<>();
        CONSTANTS.add(USER_CONST);
    }

    private DQLConstant(final String text) {
        super(text, text.toUpperCase());
    }

    private static DQLConstant evaluate(final VariableExpression expression,
            final IDQLEvaluator evaluator) {
        String name = expression.getId();
        if (!isConstant(name)) {
            return null;
        }
        return new DQLConstant(name);
    }

    private static DQLConstant evaluate(final InvokeExpression invokeExpr,
            final IDQLEvaluator evaluator) {
        List<Expression> argExprs = invokeExpr.getArguments();
        if (!Expressions.hasRequiredArgs(argExprs, 0)) {
            return null;
        }
        String op = invokeExpr.getOperation();
        if (isConstant(op)) {
            return new DQLConstant(op);
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

    public static IVariableEvaluator getVariableEvaluator() {
        return new IVariableEvaluator() {
            @Override
            public DQLExpression evaluate(final VariableExpression expression,
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
