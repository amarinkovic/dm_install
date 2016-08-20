package pro.documentum.persistence.common.query.expression.literals;

import java.util.Date;

import pro.documentum.persistence.common.query.expression.DQLExpression;
import pro.documentum.persistence.common.query.expression.literals.nulls.DQLNull;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class DQLLiteral<T> extends DQLExpression {

    private final T _value;

    protected DQLLiteral(final T value, final String text) {
        super(text);
        _value = value;
    }

    public static DQLLiteral<?> getInstance(final Object object) {
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
            return new DQLBool((Boolean) object);
        }
        if (object instanceof Iterable) {
            DQLCollection collection = new DQLCollection();
            for (Object element : (Iterable) object) {
                collection.add(getInstance(element));
            }
            return collection;
        }
        return null;
    }

    public T getValue() {
        return _value;
    }

}
