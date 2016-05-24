package pro.documentum.persistence.common.query.expression.literals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.expression.VariableExpression;

import com.documentum.fc.common.DfTime;

import pro.documentum.persistence.common.query.IDQLEvaluator;
import pro.documentum.persistence.common.query.IInvokeEvaluator;
import pro.documentum.persistence.common.query.IVariableEvaluator;
import pro.documentum.persistence.common.query.expression.DQLExpression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLDate extends DQLLiteral<Date> {

    public static final String FUNC = "DATE";

    public static final Set<String> SPECIAL_DATES;

    static {
        SPECIAL_DATES = new HashSet<>();
        SPECIAL_DATES.add("NOW");
        SPECIAL_DATES.add("TODAY");
        SPECIAL_DATES.add("YESTERDAY");
        SPECIAL_DATES.add("TOMORROW");
    }

    private DQLDate(final String value, final String format) {
        super(null, toString(value, format, true));
    }

    private DQLDate(final String specialDate) {
        super(null, toString(specialDate, null, false));
    }

    DQLDate(final Date value) {
        super(value, toString(value));
    }

    public static DQLDate getInstance(final String specialDate) {
        return new DQLDate(specialDate);
    }

    public static DQLDate getInstance(final DQLExpression value,
            final DQLExpression format) {
        if (!(format instanceof DQLString)) {
            return null;
        }
        String pattern = ((DQLString) format).getValue();
        DfTime time;
        if (value instanceof DQLString) {
            String dateValue = ((DQLString) value).getValue();
            time = new DfTime(dateValue, pattern);
        } else if (value instanceof DQLDate) {
            Date date = ((DQLDate) value).getValue();
            time = new DfTime(date);
        } else {
            return null;
        }
        return new DQLDate(time.asString(pattern), pattern);
    }

    private static String toString(final Date date) {
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return toString(format.format(date), "yyyy/mm/dd hh:mi:ss", true);
    }

    private static String toString(final String value, final String format,
            final boolean quote) {
        StringBuilder builder = new StringBuilder();
        builder.append(FUNC).append("(");
        if (quote) {
            builder.append("'");
        }
        builder.append(value);
        if (quote) {
            builder.append("'");
        }
        if (StringUtils.isNotBlank(format)) {
            builder.append(",");
            if (quote) {
                builder.append("'");
            }
            builder.append(format);
            if (quote) {
                builder.append("'");
            }
        }
        builder.append(")");
        return builder.toString();
    }

    private static boolean isDateVar(final VariableExpression expression) {
        return SPECIAL_DATES.contains(expression.getId().toUpperCase());
    }

    private static DQLExpression evaluate(final VariableExpression expression,
            final IDQLEvaluator evaluator) {
        if (!isDateVar(expression)) {
            return null;
        }
        return DQLDate.getInstance(expression.getId().toUpperCase());
    }

    private static DQLExpression evaluate(final InvokeExpression invokeExpr,
            final IDQLEvaluator evaluator) {
        String op = invokeExpr.getOperation();
        if (!FUNC.equalsIgnoreCase(op)) {
            return null;
        }

        List<Expression> dateExprs = invokeExpr.getArguments();
        if (hasRequiredArgs(dateExprs, 1)) {
            if (!isVariable(dateExprs.get(0))) {
                return null;
            }
            VariableExpression varExpr = asVariable(dateExprs.get(0));
            if (!isDateVar(varExpr)) {
                return null;
            }
            if (varExpr.evaluate(evaluator) == null) {
                return null;
            }
            return evaluator.popExpression();
        }

        if (hasRequiredArgs(dateExprs, 2)) {
            if (!isLiteralOrParameter(dateExprs.get(0))) {
                return null;
            }
            if (!isLiteralOrParameter(dateExprs.get(1))) {
                return null;
            }

            DQLExpression dateExpression = evaluator
                    .processLiteralOrParameter(dateExprs.get(0));
            if (dateExpression == null) {
                return null;
            }

            DQLExpression formatExpression = evaluator
                    .processLiteralOrParameter(dateExprs.get(1));
            if (formatExpression == null) {
                return null;
            }

            return getInstance(dateExpression, formatExpression);
        }

        return null;
    }

    public static IInvokeEvaluator getInvokeEvaluator() {
        return new IInvokeEvaluator() {
            @Override
            public DQLExpression evaluate(final InvokeExpression expression,
                    final IDQLEvaluator evaluator) {
                return DQLDate.evaluate(expression, evaluator);
            }
        };
    }

    public static IVariableEvaluator getVariableEvaluator() {
        return new IVariableEvaluator() {
            @Override
            public DQLExpression evaluate(final VariableExpression expression,
                    final IDQLEvaluator evaluator) {
                return DQLDate.evaluate(expression, evaluator);
            }
        };
    }

}
