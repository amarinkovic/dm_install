package pro.documentum.persistence.common.query.expression;

import org.apache.commons.lang.StringUtils;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLField extends DQLExpression {

    private final boolean _repeating;

    public DQLField(final String alias, final String fieldName,
            final boolean repeating) {
        super(toString(alias, fieldName));
        _repeating = repeating;
    }

    public DQLField(final String fieldName) {
        this(null, fieldName, false);
    }

    public DQLField(final String alias, final String fieldName) {
        this(alias, fieldName, false);
    }

    public DQLField(final String fieldName, final boolean repeating) {
        this(null, fieldName, repeating);
    }

    public static boolean isFieldExpression(final DQLExpression expression) {
        return expression instanceof DQLField;
    }

    public boolean isRepeating() {
        return _repeating;
    }

    private static String toString(final String alias, final String fieldName) {
        // todo: check reserved words
        if (StringUtils.isBlank(alias)) {
            return fieldName;
        }
        return alias + "." + fieldName;
    }

}
