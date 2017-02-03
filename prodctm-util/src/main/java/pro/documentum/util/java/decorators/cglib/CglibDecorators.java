package pro.documentum.util.java.decorators.cglib;

import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import pro.documentum.util.java.decorators.IDecorator;
import pro.documentum.util.java.decorators.Interfaces;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class CglibDecorators {

    private CglibDecorators() {
        super();
    }

    public static <T> T wrap(final IDecorator<T> decorator,
            final Class<?>... extraInterfaces) {
        return wrap(decorator, new CglibOverrideMethodHandler<>(decorator),
                extraInterfaces);
    }

    public static <T> T wrap(final IDecorator<T> decorator,
            final MethodInterceptor interceptor,
            final Class<?>... extraInterfaces) {
        List<Class<?>> interfaces = new ArrayList<>();
        if (extraInterfaces != null) {
            for (Class<?> cls : extraInterfaces) {
                interfaces.add(cls);
            }
        }
        return wrap(decorator, interceptor, interfaces);
    }

    public static <T> T wrap(final IDecorator<T> decorator,
            final List<Class<?>> extraInterfaces) {
        return wrap(decorator, new CglibOverrideMethodHandler<>(decorator),
                extraInterfaces);
    }

    @SuppressWarnings("unchecked")
    public static <T> T wrap(final IDecorator<T> decorator,
            final MethodInterceptor interceptor,
            final List<Class<?>> extraInterfaces) {
        final T wrapped = decorator.unwrap();
        Class<?> cls = wrapped.getClass();

        List<Class<?>> interfaces = Interfaces.getAllInterfaces(cls);
        if (extraInterfaces != null) {
            interfaces.addAll(extraInterfaces);
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setInterfaces(interfaces.toArray(new Class<?>[0]));
        enhancer.setCallback(interceptor);
        T result = (T) enhancer.create();
        decorator.setProxy(result);
        return result;
    }

}
