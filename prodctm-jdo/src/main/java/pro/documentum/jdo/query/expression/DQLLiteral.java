package pro.documentum.jdo.query.expression;

import java.util.Date;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class DQLLiteral<T> extends DQLExpression {

    protected DQLLiteral(final String value) {
        super(value);
    }

    public boolean isNull() {
        return false;
    }

    public static DQLLiteral getInstance(final Object object) {
        if (object == null) {
            return new DQLNull();
        }
        if (object instanceof String) {
            return new DQLString((String) object);
        }
        if (object instanceof Date) {
            return new DQLDate((Date) object);
        }
        if (object instanceof Number) {
            return new DQLNumber((Number) object);
        }
        if (object instanceof Boolean) {
            return new DQLBoolean((Boolean) object);
        }
        return null;
    }

}
