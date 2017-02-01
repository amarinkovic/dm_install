package pro.documentum.dfs.remote.pool;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;

import com.emc.documentum.fs.rt.context.IContextHolder;
import com.emc.documentum.fs.rt.context.IServiceContext;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class ServicePool implements IServicePool {

    private final GenericKeyedObjectPool<ServiceKey<?>, IContextHolder> _pool;

    private final PooledServiceFactory _factory;

    private ServicePool() {
        _factory = new PooledServiceFactory();
        _pool = new GenericKeyedObjectPool<>(_factory);
        _pool.setMaxTotal(-1);
        _pool.setMaxTotalPerKey(-1);
    }

    public static IServicePool getInstance() {
        return new ServicePool();
    }

    @Override
    public <T extends IContextHolder> T getService(final Class<T> serviceClass,
            final IServiceContext context) throws Exception {
        return getService(serviceClass, context, null, null);
    }

    @Override
    public <T extends IContextHolder> T getService(final Class<T> serviceClass,
            final IServiceContext context, final String moduleName,
            final String contextRoot) throws Exception {
        ServiceKey<T> key = new ServiceKey<>(serviceClass, moduleName,
                contextRoot);
        @SuppressWarnings("unchecked")
        T service = (T) _pool.borrowObject(key);
        _factory.setServiceContext(service, context);
        return PooledServiceDecorator.wrap(service, key, _pool);
    }

}
