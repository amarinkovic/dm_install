package pro.documentum.util.java.decorators.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import pro.documentum.util.java.decorators.IDecorator;
import pro.documentum.util.java.decorators.Interfaces;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class JDKDecorators {

    private JDKDecorators() {
        super();
    }

    public static <T> T proxy(final IDecorator<T> decorator,
            final Class<?>... extraInterfaces) {
        return proxy(decorator, new JDKOverrideMethodHandler<>(decorator),
                extraInterfaces);
    }

    public static <T> T proxy(final IDecorator<T> decorator,
            final InvocationHandler handler, final Class<?>... extraInterfaces) {
        List<Class<?>> interfaces = new ArrayList<>();
        if (extraInterfaces != null) {
            for (Class<?> cls : extraInterfaces) {
                interfaces.add(cls);
            }
        }
        return proxy(decorator, handler, interfaces);
    }

    public static <T> T proxy(final IDecorator<T> decorator,
            final List<Class<?>> extraInterfaces) {
        return proxy(decorator, new JDKOverrideMethodHandler<>(decorator),
                extraInterfaces);
    }

    @SuppressWarnings("unchecked")
    public static <T> T proxy(final IDecorator<T> decorator,
            final InvocationHandler handler,
            final List<Class<?>> extraInterfaces) {
        final T wrapped = decorator.unwrap();
        Class<?> cls = wrapped.getClass();
        List<Class<?>> interfaces = Interfaces.getAllInterfaces(cls);
        if (extraInterfaces != null) {
            interfaces.addAll(extraInterfaces);
        }
        T result = (T) Proxy.newProxyInstance(cls.getClassLoader(),
                interfaces.toArray(new Class<?>[interfaces.size()]), handler);
        decorator.setProxy(result);
        return result;
    }

}
