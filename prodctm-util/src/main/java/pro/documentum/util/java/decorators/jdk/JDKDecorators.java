package pro.documentum.util.java.decorators.jdk;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ClassUtils;

import pro.documentum.util.java.decorators.IDecorator;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class JDKDecorators {

    private JDKDecorators() {
        super();
    }

    public static <T> T proxy(final IDecorator<T> decorator,
            final Class<?>... extraInterfaces) {
        List<Class<?>> interfaces = new ArrayList<>();
        if (extraInterfaces != null) {
            for (Class<?> cls : extraInterfaces) {
                interfaces.add(cls);
            }
        }
        return proxy(decorator, interfaces);
    }

    @SuppressWarnings("unchecked")
    public static <T> T proxy(final IDecorator<T> decorator,
            final List<Class<?>> extraInterfaces) {
        final T wrapped = decorator.unwrap();
        Class<?> cls = wrapped.getClass();
        List<Class<?>> interfaces = ClassUtils.getAllInterfaces(cls);
        if (extraInterfaces != null) {
            interfaces.addAll(extraInterfaces);
        }
        T result = (T) Proxy.newProxyInstance(cls.getClassLoader(),
                interfaces.toArray(new Class<?>[interfaces.size()]),
                new JDKOverrideMethodHandler<>(decorator));
        decorator.setProxy(result);
        return result;
    }

}
