package pro.documentum.persistence.common;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.documentum.fc.client.IDfSessionManager;

import pro.documentum.util.sessions.Sessions;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class SessionManagerFactory extends
        BasePooledObjectFactory<IDfSessionManager> {

    public SessionManagerFactory() {
        super();
    }

    @Override
    public IDfSessionManager create() throws Exception {
        return Sessions.newSessionManager();
    }

    @Override
    public PooledObject<IDfSessionManager> wrap(
            final IDfSessionManager sessionManager) {
        return new DefaultPooledObject<>(sessionManager);
    }

    @Override
    public void passivateObject(
            final PooledObject<IDfSessionManager> pooledObject)
        throws Exception {
        IDfSessionManager sessionManager = pooledObject.getObject();
        sessionManager.clearIdentities();
    }

}
