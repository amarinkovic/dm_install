package pro.documentum.persistence.common.connection;

import java.util.Map;

import javax.transaction.xa.XAResource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.ObjectPool;
import org.datanucleus.ExecutionContext;
import org.datanucleus.PropertyNames;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.connection.AbstractConnectionFactory;
import org.datanucleus.store.connection.AbstractManagedConnection;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.connection.ManagedConnectionResourceListener;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.impl.session.ISession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;

import pro.documentum.persistence.common.StoreManagerImpl;
import pro.documentum.persistence.common.util.DNExceptions;
import pro.documentum.persistence.common.util.DfExceptions;
import pro.documentum.persistence.common.util.Nucleus;
import pro.documentum.util.auth.ICredentials;
import pro.documentum.util.logger.Logger;
import pro.documentum.util.sessions.Sessions;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class ConnectionFactoryImpl extends AbstractConnectionFactory {

    private final String _docbaseName;

    private final IDfLoginInfo _factoryLoginInfo;

    private final ObjectPool<IDfSessionManager> _sessionManagerPool;

    public ConnectionFactoryImpl(final StoreManager storeMgr,
            final String resourceName) {
        super(storeMgr, resourceName);
        Logger.debug("Creating new connection factory");
        String url = storeMgr.getConnectionURL();
        if (StringUtils.isBlank(url)) {
            throw DNExceptions
                    .noPropertySpecified(PropertyNames.PROPERTY_CONNECTION_URL);
        }
        _docbaseName = StoreManagerImpl.getDocbaseName(url);
        String userName = storeMgr.getConnectionUserName();
        String password = storeMgr.getConnectionPassword();
        if (StringUtils.isNotBlank(userName)) {
            _factoryLoginInfo = new DfLoginInfo(userName, password);
        } else {
            _factoryLoginInfo = null;
        }
        _sessionManagerPool = SessionManagerPoolFactory.getInstance()
                .createSessionManagerPool(storeMgr);
    }

    @Override
    public void close() {
        super.close();
        _sessionManagerPool.close();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public ManagedConnection createManagedConnection(
            final ExecutionContext executionContext, final Map map) {
        IDfLoginInfo loginInfo = Nucleus.extractLoginInfo(executionContext);
        if (loginInfo == null) {
            loginInfo = (IDfLoginInfo) map.get(ICredentials.class);
        }
        return new ManagedConnectionImpl(loginInfo);
    }

    public class ManagedConnectionImpl extends AbstractManagedConnection {

        private final IDfLoginInfo _connectionLoginInfo;
        private XAResource _xaRes;

        public ManagedConnectionImpl(final IDfLoginInfo loginInfo) {
            super();
            _connectionLoginInfo = loginInfo;
        }

        @Override
        public boolean closeAfterTransactionEnd() {
            return false;
        }

        @Override
        public Object getConnection() {
            if (conn != null) {
                debug("Acquired existing", (IDfSession) conn);
                return conn;
            }

            IDfSessionManager sessionManager = null;
            IDfSession session = null;
            try {
                sessionManager = obtainSessionManager();
                session = sessionManager.getSession(_docbaseName);
                Sessions.disableServerTimeout(session);
                debug("Acquired new", session);
                conn = session;
                return conn;
            } catch (DfException ex) {
                throw DfExceptions.dataStoreException(ex);
            } catch (Exception ex) {
                throw new NucleusException(ex.getMessage(), ex);
            } finally {
                if (conn == null) {
                    Sessions.release(session);
                    releaseSessionManager(sessionManager);
                }
            }
        }

        protected IDfSessionManager obtainSessionManager() throws Exception {
            IDfLoginInfo loginInfo = _connectionLoginInfo;
            if (loginInfo == null) {
                loginInfo = _factoryLoginInfo;
            }

            if (loginInfo == null) {
                throw DNExceptions
                        .noPropertySpecified(PropertyNames.PROPERTY_CONNECTION_USER_NAME);
            }

            IDfSessionManager sessionManager = _sessionManagerPool
                    .borrowObject();
            sessionManager.setIdentity(_docbaseName, loginInfo);
            return sessionManager;
        }

        protected void releaseSessionManager(
                final IDfSessionManager sessionManager) {
            if (sessionManager == null) {
                return;
            }
            try {
                _sessionManagerPool.returnObject(sessionManager);
            } catch (Exception ex) {
                Logger.error(ex);
            }
        }

        protected void releaseDfSession() {
            try {
                IDfSession session = (IDfSession) conn;
                IDfSessionManager sessionManager = session.getSessionManager();
                debug("Releasing", session);
                Sessions.enableServerTimeout((IDfSession) conn, true);
                Sessions.release(session);
                releaseSessionManager(sessionManager);
            } catch (Exception ex) {
                Logger.error(ex);
            }
        }

        @Override
        public void close() {
            if (conn == null) {
                return;
            }

            // noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < listeners.size(); i++) {
                ManagedConnectionResourceListener listener = listeners.get(i);
                listener.managedConnectionPreClose();
            }

            releaseDfSession();

            // noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < listeners.size(); i++) {
                ManagedConnectionResourceListener listener = listeners.get(i);
                listener.managedConnectionPostClose();
            }

            conn = null;
            _xaRes = null;
        }

        @Override
        public XAResource getXAResource() {
            if (_xaRes != null) {
                return _xaRes;
            }
            if (conn == null) {
                getConnection();
            }
            _xaRes = ((ISession) conn).getXAResource();
            return _xaRes;
        }

        private void debug(final String op, final IDfSession session) {
            Logger.debug("{0} session {1} for user {2}, docbase {3}", op,
                    Sessions.getSessionId(session),
                    Sessions.getLoginUserName(session),
                    Sessions.getDocbaseName(session));
        }

    }

}
