package pro.documentum.jdo.query.expression.literals;

import java.util.HashSet;
import java.util.Set;

import org.datanucleus.query.expression.VariableExpression;

import com.documentum.fc.common.DfUtil;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLString extends DQLLiteral<String> {

    public static final Set<String> SPECIAL_STRINGS;

    static {
        SPECIAL_STRINGS = new HashSet<String>();
        SPECIAL_STRINGS.add("USER");
    }

    private DQLString(final String value, final boolean quote) {
        super(value, toString(value, quote));
    }

    DQLString(final String value) {
        this(value, true);
    }

    private static String toString(final String value, final boolean quote) {
        if (!quote) {
            return value;
        }
        return "'" + DfUtil.escapeQuotedString(value) + "'";
    }

    public static boolean isLiteralVar(final VariableExpression expression) {
        return SPECIAL_STRINGS.contains(expression.getId().toUpperCase());
    }

    public static DQLString getInstance(final String value, final boolean quote) {
        return new DQLString(value, quote);
    }

}
