package pro.documentum.dfs.remote.pool;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.emc.documentum.fs.rt.context.IContextHolder;
import com.emc.documentum.fs.rt.context.IServiceContext;

import pro.documentum.util.java.decorators.cglib.CglibDecorators;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class ServiceContextDecorator implements IServiceContextDecorator {

    private IServiceContext _wrapped;

    private IServiceContext _proxy;

    private ServiceContextDecorator(final IServiceContext context) {
        _wrapped = context;
    }

    private ServiceContextDecorator() {
        this(null);
    }

    static IServiceContextDecorator of(final IContextHolder service) {
        IServiceContext context = service.getServiceContext();
        if (context instanceof IServiceContextDecorator) {
            return (IServiceContextDecorator) context;
        }
        return null;
    }

    static IServiceContext wrap(final IServiceContext context) {
        Class<?> cls = context.getClass();
        for (Method mtd : cls.getDeclaredMethods()) {
            int modifiers = mtd.getModifiers();
            if (!Modifier.isPublic(modifiers)) {
                continue;
            }
            if (!Modifier.isFinal(modifiers)) {
                continue;
            }
            throw new RuntimeException("Method " + mtd.getName() + " of class "
                    + cls.getName() + " has a final modifier");
        }
        return CglibDecorators.wrap(new ServiceContextDecorator(context),
                IServiceContextDecorator.class);
    }

    @Override
    public void setWrapped(final IServiceContext wrapped) {
        _wrapped = wrapped;
    }

    @Override
    public IServiceContext unwrap() {
        return _wrapped;
    }

    @Override
    public void setProxy(final IServiceContext proxy) {
        _proxy = proxy;
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public IServiceContext clone() {
        _wrapped = _wrapped.clone();
        return _proxy;
    }

}
