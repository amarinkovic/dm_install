package pro.documentum.persistence.common.query.expression.literals.nulls;

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
import pro.documentum.persistence.common.query.expression.literals.DQLLiteral;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLNull extends DQLLiteral<Void> {

    public static final String NULL = "NULL";

    public static final String NULLDATE = "NULLDATE";

    public static final String NULLSTRING = "NULLSTRING";

    public static final Set<String> SPECIAL_NULLS;

    static {
        SPECIAL_NULLS = new HashSet<>();
        SPECIAL_NULLS.add(NULL);
        SPECIAL_NULLS.add(NULLDATE);
        SPECIAL_NULLS.add(NULLSTRING);
    }

    public DQLNull() {
        super(null, NULL);
    }

    private static DQLExpression evaluate(final VariableExpression expression,
            final IDQLEvaluator evaluator) {
        String name = expression.getId();
        if (!isSpecialNull(name)) {
            return null;
        }
        return getInstance(name);
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
        return null;
    }

    public static DQLLiteral<?> getInstance(final String value) {
        if (NULL.equalsIgnoreCase(value)) {
            return new DQLNull();
        } else if (NULLSTRING.equalsIgnoreCase(value)) {
            return new DQLNullString();
        } else if (NULLDATE.equalsIgnoreCase(value)) {
            return new DQLNullDate();
        }
        return null;
    }

    public static boolean isSpecialNull(final String value) {
        if (value == null) {
            return false;
        }
        return SPECIAL_NULLS.contains(value.toUpperCase());
    }

    public static IVariableEvaluator getVariableEvaluator() {
        return new IVariableEvaluator() {
            @Override
            public DQLExpression evaluate(final VariableExpression expression,
                    final IDQLEvaluator evaluator) {
                return DQLNull.evaluate(expression, evaluator);
            }
        };
    }

    public static IInvokeEvaluator getInvokeEvaluator() {
        return new IInvokeEvaluator() {
            @Override
            public DQLExpression evaluate(final InvokeExpression expression,
                    final IDQLEvaluator evaluator) {
                return DQLNull.evaluate(expression, evaluator);
            }
        };
    }

    @Override
    public boolean isNull() {
        return true;
    }

}
