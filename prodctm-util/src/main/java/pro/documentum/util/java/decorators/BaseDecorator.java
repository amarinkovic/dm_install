package pro.documentum.util.java.decorators;

import java.util.Objects;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class BaseDecorator<T> implements IDecorator<T> {

    private final T _wrapped;

    private T _proxy;

    public BaseDecorator(final T wrapped) {
        _wrapped = Objects.requireNonNull(wrapped);
    }

    @Override
    public final T unwrap() {
        return _wrapped;
    }

    @Override
    public void setProxy(final T proxy) {
        _proxy = proxy;
    }

    @Override
    public final int hashCode() {
        return _wrapped.hashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof IDecorator)) {
            return false;
        }
        return _wrapped.equals(((IDecorator) obj).unwrap());
    }

}
