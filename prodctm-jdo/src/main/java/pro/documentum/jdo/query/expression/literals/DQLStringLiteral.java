package pro.documentum.jdo.query.expression.literals;

import java.util.HashSet;
import java.util.Set;

import org.datanucleus.query.expression.VariableExpression;

import com.documentum.fc.common.DfUtil;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLStringLiteral extends DQLLiteral<String> {

    public static final Set<String> SPECIAL_STRINGS;

    static {
        SPECIAL_STRINGS = new HashSet<String>();
        SPECIAL_STRINGS.add("USER");
    }

    private DQLStringLiteral(final String value, final boolean quote) {
        super(value, toString(value, quote));
    }

    DQLStringLiteral(final String value) {
        this(value, true);
    }

    private static String toString(final String value, final boolean quote) {
        if (!quote) {
            return value;
        }
        return "'" + DfUtil.escapeQuotedString(value) + "'";
    }

    public static boolean isLiteralExpression(
            final VariableExpression expression) {
        return SPECIAL_STRINGS.contains(expression.getId().toUpperCase());
    }

    public static DQLStringLiteral getInstance(final String value,
            final boolean quote) {
        return new DQLStringLiteral(value, quote);
    }

}
