package pro.documentum.util.java.decorators;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.documentum.thirdparty.javassist.util.proxy.MethodHandler;
import com.documentum.thirdparty.javassist.util.proxy.Proxy;
import com.documentum.thirdparty.javassist.util.proxy.ProxyFactory;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class Decorators {

    private static final Objenesis OBJENESIS;

    static {
        OBJENESIS = new ObjenesisStd();
    }

    private Decorators() {
        super();
    }

    @SuppressWarnings("unchecked")
    public static <T> T wrap(final IDecorator<T> decorator) {
        final T wrapped = decorator.unwrap();
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(wrapped.getClass());
        factory.setFilter(new AllMethodsFilter());
        MethodHandler handler = new OverrideMethodHandler(decorator);
        Class<T> cls = factory.createClass();
        T result = OBJENESIS.getInstantiatorOf(cls).newInstance();
        ((Proxy) result).setHandler(handler);
        return result;
    }

}
