package pro.documentum.jdo.query.expression.literals;

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

import pro.documentum.jdo.query.expression.DQLExpression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLDate extends DQLLiteral<Date> {

    public static final String FUNC = "DATE";

    public static final Set<String> SPECIAL_DATES;

    static {
        SPECIAL_DATES = new HashSet<String>();
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

    public static boolean isSpecialDateExpression(
            final VariableExpression expression) {
        return SPECIAL_DATES.contains(expression.getId().toUpperCase());
    }

    public static boolean isDate(final InvokeExpression invokeExpr) {
        String op = invokeExpr.getOperation();
        if (!FUNC.equalsIgnoreCase(op)) {
            return false;
        }

        List<Expression> dateExprs = invokeExpr.getArguments();
        if (dateExprs == null || dateExprs.isEmpty()) {
            return false;
        }
        if (dateExprs.size() > 2) {
            return false;
        }
        if (isSpecialDate(dateExprs)) {
            return true;
        }
        return isDateWithFormat(dateExprs);
    }

    private static boolean isDateWithFormat(final List<Expression> dateExprs) {
        if (dateExprs.size() != 2) {
            return false;
        }
        Expression valueExpr = dateExprs.get(0);
        Expression formatExpr = dateExprs.get(1);
        return isLiteralOrParameter(valueExpr)
                && isLiteralOrParameter(formatExpr);
    }

    private static boolean isSpecialDate(final List<Expression> dateExprs) {
        if (dateExprs.size() > 1) {
            return false;
        }
        Expression valueExpr = dateExprs.get(0);
        if (!isVariable(valueExpr)) {
            return false;
        }
        return isSpecialDateExpression(asVariable(valueExpr));
    }

}
