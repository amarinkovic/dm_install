package pro.documentum.jdo.query.expression.literals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.datanucleus.query.expression.VariableExpression;

import com.documentum.fc.common.DfTime;

import pro.documentum.jdo.query.expression.DQLExpression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLDateLiteral extends DQLLiteral<Date> {

    public static final Set<String> SPECIAL_DATES;

    static {
        SPECIAL_DATES = new HashSet<String>();
        SPECIAL_DATES.add("NOW");
        SPECIAL_DATES.add("TODAY");
        SPECIAL_DATES.add("YESTERDAY");
        SPECIAL_DATES.add("TOMORROW");
    }

    private DQLDateLiteral(final String value, final String format) {
        super(null, toString(value, format, true));
    }

    private DQLDateLiteral(final String specialDate) {
        super(null, toString(specialDate, null, false));
    }

    DQLDateLiteral(final Date value) {
        super(value, toString(value));
    }

    public static DQLDateLiteral getInstance(final DQLExpression value,
            final DQLExpression format) {
        if (!(format instanceof DQLStringLiteral)) {
            return null;
        }
        String pattern = ((DQLStringLiteral) format).getValue();
        DfTime time;
        if (value instanceof DQLStringLiteral) {
            String dateValue = ((DQLStringLiteral) value).getValue();
            time = new DfTime(dateValue, pattern);
        } else if (value instanceof DQLDateLiteral) {
            Date date = ((DQLDateLiteral) value).getValue();
            time = new DfTime(date);
        } else {
            return null;
        }
        return new DQLDateLiteral(time.asString(pattern), pattern);
    }

    private static String toString(final Date date) {
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return toString(format.format(date), "yyyy/mm/dd hh:mi:ss", true);
    }

    private static String toString(final String value, final String format,
            final boolean quote) {
        StringBuilder builder = new StringBuilder();
        builder.append("DATE(");
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

    public static DQLDateLiteral getInstance(final String specialDate) {
        return new DQLDateLiteral(specialDate);
    }

}
