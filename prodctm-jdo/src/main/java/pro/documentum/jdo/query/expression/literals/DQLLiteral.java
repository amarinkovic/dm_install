package pro.documentum.jdo.query.expression.literals;

import java.util.Date;

import pro.documentum.jdo.query.expression.DQLExpression;
import pro.documentum.jdo.query.expression.literals.nulls.DQLNullLiteral;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class DQLLiteral<T> extends DQLExpression {

    private final T _value;

    protected DQLLiteral(final T value, final String text) {
        super(text);
        _value = value;
    }

    public T getValue() {
        return _value;
    }

    public static DQLLiteral getInstance(final Object object) {
        if (object == null) {
            return new DQLNullLiteral();
        }
        if (object instanceof String) {
            return new DQLStringLiteral((String) object);
        }
        if (object instanceof Date) {
            return new DQLDateLiteral((Date) object);
        }
        if (object instanceof Number) {
            return new DQLNumberLiteral((Number) object);
        }
        if (object instanceof Boolean) {
            return new DQLBooleanLiteral((Boolean) object);
        }
        return null;
    }

}
