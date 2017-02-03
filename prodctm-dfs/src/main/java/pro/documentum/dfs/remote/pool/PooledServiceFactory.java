package pro.documentum.dfs.remote.pool;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.emc.documentum.fs.rt.context.ContextFactory;
import com.emc.documentum.fs.rt.context.IContextHolder;
import com.emc.documentum.fs.rt.context.IServiceContext;
import com.emc.documentum.fs.rt.context.ServiceFactory;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PooledServiceFactory extends
        BaseKeyedPooledObjectFactory<ServiceKey<?>, IContextHolder> {

    PooledServiceFactory() {
        super();
    }

    @Override
    public IContextHolder create(final ServiceKey<?> key) throws Exception {
        IServiceContext context = ServiceContextDecorator.wrap(ContextFactory
                .getInstance().newContext());
        return ServiceFactory.getInstance().getRemoteService(
                key.getServiceClass(), context, key.getServiceModule(),
                key.getContextRoot());
    }

    @Override
    public PooledObject<IContextHolder> wrap(final IContextHolder value) {
        return new DefaultPooledObject<>(value);
    }

    @Override
    public void passivateObject(final ServiceKey<?> key,
            final PooledObject<IContextHolder> p) throws Exception {
        IContextHolder service = p.getObject();
        setServiceContext(service, null);
    }

    void setServiceContext(final IContextHolder service,
            final IServiceContext context) {
        ServiceContextDecorator.of(service).setWrapped(context);
    }

}
