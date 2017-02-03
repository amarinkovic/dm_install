package pro.documentum.util.java.decorators.javassist;

import java.lang.reflect.Method;

import com.documentum.thirdparty.javassist.util.proxy.MethodFilter;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
final class AllMethodsFilter implements MethodFilter {

    AllMethodsFilter() {
        super();
    }

    @Override
    public boolean isHandled(final Method method) {
        return true;
    }

}
