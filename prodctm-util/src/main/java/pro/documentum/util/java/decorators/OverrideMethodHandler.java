package pro.documentum.util.java.decorators;

import java.lang.reflect.Method;
import java.util.Objects;

import com.documentum.thirdparty.javassist.util.proxy.MethodHandler;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
class OverrideMethodHandler<T> implements MethodHandler {

    private final IDecorator<T> _decorator;

    OverrideMethodHandler(final IDecorator<T> decorator) {
        _decorator = Objects.requireNonNull(decorator);
    }

    private static Method getMethod(final Object object, final String name,
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
    public Object invoke(final Object self, final Method thisMethod,
            final Method proceed, final Object[] args) throws Throwable {
        T wrapped = _decorator.unwrap();
        Method override = getMethod(_decorator, thisMethod.getName(),
                thisMethod.getParameterTypes());
        if (override == null) {
            return thisMethod.invoke(wrapped, args);
        }
        return override.invoke(_decorator, args);
    }

}
