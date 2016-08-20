package pro.documentum.persistence.common.query.expression.literals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pro.documentum.persistence.common.query.expression.DQLExpression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DQLCollection extends DQLLiteral<DQLLiteral<?>> {

    private final List<DQLLiteral<?>> _notNullElements = new ArrayList<>();

    private final List<DQLLiteral<?>> _nullElements = new ArrayList<>();

    public DQLCollection() {
        this(null);
    }

    public DQLCollection(final Collection<DQLLiteral<?>> value) {
        super(null, null);
        if (value != null) {
            for (DQLLiteral<?> literal : value) {
                add(literal);
            }
        }
    }

    public static boolean isCollection(final DQLExpression expression) {
        return expression instanceof DQLCollection;
    }

    public void add(final DQLLiteral<?> literal) {
        if (literal.isNull()) {
            _nullElements.add(literal);
        } else {
            _notNullElements.add(literal);
        }
    }

    public List<DQLLiteral<?>> getNotNullElements() {
        return _notNullElements;
    }

    public List<DQLLiteral<?>> getNullElements() {
        return _nullElements;
    }

    public boolean hasNotNullElements() {
        return !_notNullElements.isEmpty();
    }

    public boolean hasNullElements() {
        return !_nullElements.isEmpty();
    }

}
