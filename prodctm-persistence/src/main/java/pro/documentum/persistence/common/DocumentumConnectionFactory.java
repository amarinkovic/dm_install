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
import pro.documentum.persistence.jdo.DocumentumPersistenceManager;
import pro.documentum.util.logger.Logger;
import pro.documentum.util.sessions.Sessions;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DocumentumConnectionFactory extends AbstractConnectionFactory {

    private final String _docbaseName;

    private final IDfSessionManager _sessionManager;

    public DocumentumConnectionFactory(final StoreManager storeMgr,
            final String resourceName) {
        super(storeMgr, resourceName);
        try {
            Logger.debug("Creating new connection factory");
            String url = storeMgr.getConnectionURL();
            if (StringUtils.isBlank(url)) {
                throw DNExceptions
                        .noPropertySpecified(PropertyNames.PROPERTY_CONNECTION_URL);
            }
            _docbaseName = DocumentumStoreManager.getDocbaseName(url);
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
        IDfLoginInfo loginInfo = (IDfLoginInfo) map
                .get(DocumentumPersistenceManager.OPTION_LOGININFO);
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
            if (conn == null) {
                obtainNewConnection();
            } else {
                IDfSession session = (IDfSession) conn;
                Logger.debug("Acquired existing session {0} for user {1}, "
                        + "docbase {2}", Sessions.getSessionId(session),
                        Sessions.getLoginUserName(session),
                        Sessions.getDocbaseName(session));
            }
            return conn;
        }

        protected void obtainNewConnection() {
            try {
                if (conn == null) {
                    if (_loginInfo == null) {
                        if (_sessionManager == null) {
                            throw DNExceptions
                                    .noPropertySpecified(PropertyNames.PROPERTY_CONNECTION_USER_NAME);
                        }
                        conn = Sessions.brandNew(_sessionManager, _docbaseName);
                    } else {
                        conn = Sessions.brandNew(_loginInfo, _docbaseName);
                    }
                    IDfSession session = (IDfSession) conn;
                    Sessions.disableServerTimeout(session);
                    Logger.debug("Acquired new session {0} for user {1}, "
                            + "docbase {2}", Sessions.getSessionId(session),
                            Sessions.getLoginUserName(session),
                            Sessions.getDocbaseName(session));
                }
            } catch (DfException ex) {
                throw DfExceptions.dataStoreException(ex);
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

            IDfSession session = (IDfSession) conn;
            try {
                Logger.debug("Releasing session {0} for user {1}, "
                        + "docbase {2}", Sessions.getSessionId(session),
                        Sessions.getLoginUserName(session),
                        Sessions.getDocbaseName(session));
                Sessions.enableServerTimeout((IDfSession) conn);
                Sessions.release((IDfSession) conn);
            } catch (DfException ex) {
                Logger.error(ex);
            }
            // noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < listeners.size(); i++) {
                ManagedConnectionResourceListener listener = listeners.get(i);
                listener.managedConnectionPostClose();
            }

            this.conn = null;
            this._xaRes = null;
        }

        @Override
        public XAResource getXAResource() {
            if (_xaRes != null) {
                return _xaRes;
            }
            if (conn == null) {
                obtainNewConnection();
            }
            _xaRes = ((ISession) conn).getXAResource();
            return _xaRes;
        }

    }

}
