package pro.documentum.util.java.decorators.javassist;

import java.util.ArrayList;
import java.util.List;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.documentum.thirdparty.javassist.util.proxy.MethodHandler;
import com.documentum.thirdparty.javassist.util.proxy.ProxyFactory;
import com.documentum.thirdparty.javassist.util.proxy.ProxyObject;

import pro.documentum.util.java.decorators.IDecorator;
import pro.documentum.util.java.decorators.Interfaces;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class JavassistDecorators {

    private static final Objenesis OBJENESIS;

    static {
        OBJENESIS = new ObjenesisStd();
    }

    private JavassistDecorators() {
        super();
    }

    public static <T> T wrap(final IDecorator<T> decorator,
            final Class<?>... extraInterfaces) {
        return wrap(decorator, new JavassistOverrideMethodHandler<>(decorator),
                extraInterfaces);
    }

    public static <T> T wrap(final IDecorator<T> decorator,
            final MethodHandler handler, final Class<?>... extraInterfaces) {
        List<Class<?>> interfaces = new ArrayList<>();
        if (extraInterfaces != null) {
            for (Class<?> cls : extraInterfaces) {
                interfaces.add(cls);
            }
        }
        return wrap(decorator, handler, interfaces);
    }

    public static <T> T wrap(final IDecorator<T> decorator,
            final List<Class<?>> extraInterfaces) {
        return wrap(decorator, new JavassistOverrideMethodHandler<>(decorator),
                extraInterfaces);
    }

    @SuppressWarnings("unchecked")
    public static <T> T wrap(final IDecorator<T> decorator,
            final MethodHandler handler, final List<Class<?>> extraInterfaces) {
        try {
            final T wrapped = decorator.unwrap();
            Class<?> cls = wrapped.getClass();

            List<Class<?>> interfaces = Interfaces.getAllInterfaces(cls);
            if (extraInterfaces != null) {
                interfaces.addAll(extraInterfaces);
            }

            ProxyFactory factory = new ProxyFactory();
            factory.setSuperclass(cls);
            factory.setInterfaces(interfaces.toArray(new Class<?>[0]));
            factory.setFilter(new AllMethodsFilter());
            Class<T> proxyClass = factory.createClass();
            T result = OBJENESIS.getInstantiatorOf(proxyClass).newInstance();
            ((ProxyObject) result).setHandler(handler);
            decorator.setProxy(result);
            return result;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
