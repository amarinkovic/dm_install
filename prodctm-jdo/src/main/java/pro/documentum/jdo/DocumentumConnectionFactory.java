package pro.documentum.jdo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.xa.XAResource;

import org.apache.commons.lang.StringUtils;
import org.datanucleus.ExecutionContext;
import org.datanucleus.PropertyNames;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.connection.AbstractConnectionFactory;
import org.datanucleus.store.connection.AbstractManagedConnection;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.connection.ManagedConnectionResourceListener;
import org.datanucleus.util.NucleusLogger;

import com.documentum.com.DfClientX;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.impl.session.ISession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;

import pro.documentum.jdo.util.DfExceptions;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DocumentumConnectionFactory extends AbstractConnectionFactory {

    private String _docbaseName;

    private IDfSessionManager _sessionManager;

    public DocumentumConnectionFactory(final StoreManager storeMgr,
            final String resourceName) {
        super(storeMgr, resourceName);
        try {
            String url = storeMgr.getConnectionURL();
            if (StringUtils.isBlank(url)) {
                throw new NucleusException(
                        "You haven't specified persistence property '"
                                + PropertyNames.PROPERTY_CONNECTION_URL
                                + "' (or alias)");
            }
            _docbaseName = url
                    .substring(DocumentumStoreManager.PREFIX.length() + 1);
            List<IDfLoginInfo> credentials = null;
            String userName = storeMgr.getConnectionUserName();
            String password = storeMgr.getConnectionPassword();
            if (StringUtils.isNotBlank(userName)) {
                IDfLoginInfo loginInfo = new DfLoginInfo(userName, password);
                credentials = new ArrayList<IDfLoginInfo>();
                credentials.add(loginInfo);
            }
            _sessionManager = new DfClientX().getLocalClient()
                    .newSessionManager();
            _sessionManager.setIdentity(_docbaseName, credentials.get(0));
        } catch (DfException e) {
            throw DfExceptions.dataStoreException(e);
        }
    }

    public String getDocbaseName() {
        return _docbaseName;
    }

    public IDfSessionManager getSessionManager() {
        return _sessionManager;
    }

    @Override
    public ManagedConnection createManagedConnection(
            final ExecutionContext executionContext, final Map map) {
        return new ManagedConnectionImpl();
    }

    public class ManagedConnectionImpl extends AbstractManagedConnection {

        private XAResource _xaRes;

        public ManagedConnectionImpl() {
            super();
        }

        @Override
        public boolean closeAfterTransactionEnd() {
            return false;
        }

        @Override
        public Object getConnection() {
            if (conn == null) {
                obtainNewConnection();
            }
            return conn;
        }

        protected void obtainNewConnection() {
            try {
                if (conn == null) {
                    conn = getSessionManager().getSession(getDocbaseName());
                    NucleusLogger.CONNECTION
                            .debug("Created new documentum session");
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

            getSessionManager().release((IDfSession) conn);

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
