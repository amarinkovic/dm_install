package pro.documentum.util.java.decorators;

import java.util.Objects;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class BaseDecorator<T> implements IDecorator<T> {

    private final T _wrapped;

    public BaseDecorator(final T wrapped) {
        _wrapped = Objects.requireNonNull(wrapped);
    }

    @Override
    public final T unwrap() {
        return _wrapped;
    }

    @Override
    public int hashCode() {
        return _wrapped.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof IDecorator) {
            return _wrapped.equals(((IDecorator) obj).unwrap());
        }
        return _wrapped.equals(obj);
    }

}
