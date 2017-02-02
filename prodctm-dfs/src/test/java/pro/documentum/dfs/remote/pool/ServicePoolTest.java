package pro.documentum.dfs.remote.pool;

import java.util.Arrays;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.emc.documentum.fs.datamodel.core.DataPackage;
import com.emc.documentum.fs.datamodel.core.ObjectIdentity;
import com.emc.documentum.fs.datamodel.core.ObjectIdentitySet;
import com.emc.documentum.fs.datamodel.core.Qualification;
import com.emc.documentum.fs.datamodel.core.context.Identity;
import com.emc.documentum.fs.datamodel.core.context.RepositoryIdentity;
import com.emc.documentum.fs.rt.AuthenticationException;
import com.emc.documentum.fs.rt.context.ContextFactory;
import com.emc.documentum.fs.rt.context.IServiceContext;
import com.emc.documentum.fs.services.core.client.IObjectService;

import pro.documentum.junit.DfcTestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class ServicePoolTest extends DfcTestSupport {

    @Test
    public void testPool() throws Exception {
        IServicePool pool = ServicePool.getInstance();
        IObjectService service = pool.getService(IObjectService.class, null);
        assertNotNull(service);
        assertNotNull(service.getServiceContext());
        assertTrue(service.getServiceContext() instanceof IServiceContextDecorator);
        assertTrue(service instanceof IPooledService);
        ((IPooledService) service).returnToPool();
        IObjectService service1 = pool.getService(IObjectService.class, null);
        assertFalse(service == service1);
    }

    @Test
    public void testObjectService() throws Exception {
        IServicePool pool = ServicePool.getInstance();
        IServiceContext context = ContextFactory.getInstance().newContext();
        Identity identity = new RepositoryIdentity(getSession()
                .getDocbaseName(), getLoginInfo().getUser(), getLoginInfo()
                .getPassword(), null);
        context.setIdentities(Arrays.asList(identity));
        IObjectService objectService = pool.getService(IObjectService.class,
                context);
        ObjectIdentity<Qualification<String>> objectIdentity = new ObjectIdentity<>(
                new Qualification<>("dm_server_config"), getSession()
                        .getDocbaseName());
        assertEquals(1, objectService.getServiceContext().getIdentityCount());
        assertEquals(getSession().getDocbaseName(),
                ((RepositoryIdentity) objectService.getServiceContext()
                        .getIdentity(0)).getRepositoryName());
        objectService.getServiceContext().addIdentity(identity);
        DataPackage dataPackage = objectService.get(new ObjectIdentitySet(
                objectIdentity), null);
        assertNotNull(dataPackage);

        IServiceContext newContext = ContextFactory.getInstance().newContext();
        identity = new RepositoryIdentity(getSession().getDocbaseName(),
                getLoginInfo().getUser(), RandomStringUtils.random(20), null);
        newContext.setIdentities(Arrays.asList(identity));

        Exception exception = null;
        try {
            ServiceContextDecorator.of(objectService).setWrapped(newContext);
            dataPackage = objectService.get(new ObjectIdentitySet(
                    objectIdentity), null);
        } catch (Exception ex) {
            exception = ex;
        }

        assertNotNull(exception);
        assertTrue(exception instanceof AuthenticationException);

        ((IPooledService) objectService).returnToPool();

    }

}
