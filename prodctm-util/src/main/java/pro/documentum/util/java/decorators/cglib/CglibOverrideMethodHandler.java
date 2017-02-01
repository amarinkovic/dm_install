package pro.documentum.util.java.decorators.cglib;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import pro.documentum.util.java.decorators.IDecorator;
import pro.documentum.util.java.decorators.jdk.JDKOverrideMethodHandler;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
class CglibOverrideMethodHandler<T> extends JDKOverrideMethodHandler<T> implements
        MethodInterceptor {

    CglibOverrideMethodHandler(final IDecorator<T> decorator) {
        super(decorator);
    }

    @Override
    public Object intercept(final Object self, final Method method,
            final Object[] args, final MethodProxy methodProxy)
        throws Throwable {
        return invoke(self, method, args);
    }

}
