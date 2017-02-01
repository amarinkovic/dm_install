package pro.documentum.util.java.decorators;

import java.lang.reflect.Method;

import com.documentum.thirdparty.javassist.util.proxy.MethodFilter;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class AllMethodsFilter implements MethodFilter {

    public AllMethodsFilter() {
        super();
    }

    @Override
    public boolean isHandled(final Method method) {
        return true;
    }

}
