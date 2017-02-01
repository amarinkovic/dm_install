package pro.documentum.dfs.remote.pool;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.pool2.KeyedObjectPool;

import com.emc.documentum.fs.rt.context.IContextHolder;

import pro.documentum.util.ISessionInvoker;
import pro.documentum.util.java.decorators.BaseDecorator;
import pro.documentum.util.java.decorators.jdk.JDKDecorators;
import pro.documentum.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class PooledServiceDecorator<T extends IContextHolder> extends
        BaseDecorator<T> implements IPooledService {

    private final ISessionInvoker<Void, T, Exception> _returnInvoker;

    private PooledServiceDecorator(final T wrapped,
            final ISessionInvoker<Void, T, Exception> returnInvoker) {
        super(wrapped);
        _returnInvoker = Objects.requireNonNull(returnInvoker);
    }

    @Override
    public void close() throws IOException {
        try {
            _returnInvoker.invoke(unwrap());
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void returnToPool() {
        try {
            close();
        } catch (IOException ex) {
            Logger.error(ex);
        }
    }

    static <T extends IContextHolder> T wrap(final T service,
            final ServiceKey<?> serviceKey,
            final KeyedObjectPool<ServiceKey<?>, IContextHolder> pool) {
        PooledServiceDecorator<T> decorator = new PooledServiceDecorator<T>(
                service, new ISessionInvoker<Void, T, Exception>() {
                    @Override
                    public Void invoke(final IContextHolder service)
                        throws Exception {
                        pool.returnObject(serviceKey, service);
                        return null;
                    }
                });
        return JDKDecorators.proxy(decorator, IPooledService.class);
    }

}
