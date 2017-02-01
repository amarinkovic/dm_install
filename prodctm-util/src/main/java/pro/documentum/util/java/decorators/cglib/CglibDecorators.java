package pro.documentum.util.java.decorators.cglib;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ClassUtils;

import net.sf.cglib.proxy.Enhancer;

import pro.documentum.util.java.decorators.IDecorator;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class CglibDecorators {

    private CglibDecorators() {
        super();
    }

    public static <T> T wrap(final IDecorator<T> decorator,
            final Class<?>... extraInterfaces) {
        List<Class<?>> interfaces = new ArrayList<>();
        if (extraInterfaces != null) {
            for (Class<?> cls : extraInterfaces) {
                interfaces.add(cls);
            }
        }
        return wrap(decorator, interfaces);
    }

    @SuppressWarnings("unchecked")
    public static <T> T wrap(final IDecorator<T> decorator,
            final List<Class<?>> extraInterfaces) {
        final T wrapped = decorator.unwrap();
        Class<?> cls = wrapped.getClass();

        List<Class<?>> interfaces = ClassUtils.getAllInterfaces(cls);
        if (extraInterfaces != null) {
            interfaces.addAll(extraInterfaces);
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setInterfaces(interfaces.toArray(new Class<?>[0]));
        CglibOverrideMethodHandler<T> proxyInterceptor = new CglibOverrideMethodHandler<T>(
                decorator);
        enhancer.setCallback(proxyInterceptor);
        T result = (T) enhancer.create();
        decorator.setProxy(result);
        return result;
    }

}
