package pro.documentum.util.java.decorators.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import pro.documentum.util.java.decorators.IDecorator;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class JDKOverrideMethodHandler<T> implements InvocationHandler {

    private final IDecorator<T> _decorator;

    public JDKOverrideMethodHandler(final IDecorator<T> decorator) {
        _decorator = Objects.requireNonNull(decorator);
    }

    protected static boolean isEquals(final Method method) {
        if (!"equals".equals(method.getName())) {
            return false;
        }
        Class<?>[] types = method.getParameterTypes();
        if (types.length != 1) {
            return false;
        }
        return types[0] == Object.class;
    }

    protected static Method getMethod(final Object object, final String name,
            final Class<?>[] parameterTypes) {
        try {
            Method result = object.getClass().getMethod(name, parameterTypes);
            if (!result.isAccessible()) {
                result.setAccessible(true);
            }
            return result;
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    @Override
    public Object invoke(final Object proxy, final Method method,
            final Object[] args) throws Throwable {
        T wrapped = getDecorator().unwrap();
        if (isEquals(method)) {
            return proxy == args[0];
        }
        Method override = getMethod(getDecorator(), method.getName(),
                method.getParameterTypes());
        try {
            if (override == null) {
                return method.invoke(wrapped, args);
            }
            return override.invoke(getDecorator(), args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    protected IDecorator<T> getDecorator() {
        return _decorator;
    }

}
