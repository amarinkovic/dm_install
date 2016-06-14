package pro.documentum.persistence.common;

import java.util.Map;

import javax.transaction.xa.XAResource;

import org.apache.commons.lang.StringUtils;
import org.datanucleus.ExecutionContext;
import org.datanucleus.PropertyNames;
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

import pro.documentum.persistence.common.util.DNExceptions;
import pro.documentum.persistence.common.util.DfExceptions;
import pro.documentum.persistence.common.util.Nucleus;
import pro.documentum.util.logger.Logger;
import pro.documentum.util.sessions.Sessions;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class ConnectionFactoryImpl extends AbstractConnectionFactory {

    private final String _docbaseName;

    private final IDfSessionManager _sessionManager;

    public ConnectionFactoryImpl(final StoreManager storeMgr,
            final String resourceName) {
        super(storeMgr, resourceName);
        try {
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
                IDfLoginInfo loginInfo = new DfLoginInfo(userName, password);
                _sessionManager = Sessions.newSessionManager(loginInfo,
                        _docbaseName);
            } else {
                _sessionManager = null;
            }
        } catch (DfException e) {
            throw DfExceptions.dataStoreException(e);
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public ManagedConnection createManagedConnection(
            final ExecutionContext executionContext, final Map map) {
        IDfLoginInfo loginInfo = Nucleus.extractLoginInfo(executionContext);
        if (loginInfo == null) {
            loginInfo = (IDfLoginInfo) map
                    .get(ICredentialsHolder.OPTION_LOGININFO);
        }
        return new ManagedConnectionImpl(loginInfo);
    }

    public class ManagedConnectionImpl extends AbstractManagedConnection {

        private final IDfLoginInfo _loginInfo;
        private XAResource _xaRes;

        public ManagedConnectionImpl(final IDfLoginInfo loginInfo) {
            super();
            _loginInfo = loginInfo;
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

            IDfSession session = null;
            try {
                session = obtainNewDfSession();
                Sessions.disableServerTimeout(session);
                debug("Acquired new", session);
                conn = session;
                return conn;
            } catch (DfException ex) {
                throw DfExceptions.dataStoreException(ex);
            } finally {
                if (conn == null) {
                    Sessions.release(session);
                }
            }
        }

        protected IDfSession obtainNewDfSession() throws DfException {
            if (_loginInfo != null) {
                return Sessions.brandNew(_loginInfo, _docbaseName);
            }
            if (_sessionManager != null) {
                return Sessions.brandNew(_sessionManager, _docbaseName);
            }
            throw DNExceptions
                    .noPropertySpecified(PropertyNames.PROPERTY_CONNECTION_USER_NAME);
        }

        protected void releaseDfSession() {
            try {
                IDfSession session = (IDfSession) conn;
                debug("Releasing", session);
                Sessions.enableServerTimeout((IDfSession) conn, true);
                Sessions.release((IDfSession) conn);
            } catch (DfException ex) {
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
