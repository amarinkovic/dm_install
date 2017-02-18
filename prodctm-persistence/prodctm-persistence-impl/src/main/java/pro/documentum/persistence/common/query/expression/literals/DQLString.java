package pro.documentum.persistence.common.query.expression.literals;

import com.documentum.fc.common.DfUtil;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLString extends DQLLiteral<String> {

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

    public static DQLString getInstance(final String value, final boolean quote) {
        return new DQLString(value, quote);
    }

}
