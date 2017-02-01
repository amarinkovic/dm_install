package pro.documentum.util.java.decorators.javassist;

import java.lang.reflect.Method;

import com.documentum.thirdparty.javassist.util.proxy.MethodHandler;

import pro.documentum.util.java.decorators.IDecorator;
import pro.documentum.util.java.decorators.jdk.JDKOverrideMethodHandler;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
class JavassistOverrideMethodHandler<T> extends JDKOverrideMethodHandler<T>
        implements MethodHandler {

    JavassistOverrideMethodHandler(final IDecorator<T> decorator) {
        super(decorator);
    }

    @Override
    public Object invoke(final Object self, final Method thisMethod,
            final Method proceed, final Object[] args) throws Throwable {
        return invoke(self, thisMethod, args);
    }

}
