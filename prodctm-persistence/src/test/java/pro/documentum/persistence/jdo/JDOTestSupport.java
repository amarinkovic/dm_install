package pro.documentum.persistence.jdo;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import javax.jdo.datastore.JDOConnection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;

import pro.documentum.junit.auth.IDocumentumCredentials;
import pro.documentum.junit.auth.PropertiesCredentialManager;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class JDOTestSupport extends Assert {

    private PersistenceManager _pm;

    private IDfSession _underneathSession;

    private IDfLoginInfo _loginInfo;

    @Before
    public final void setUp() throws Exception {
        IDocumentumCredentials credentials = new PropertiesCredentialManager(
                null).getCredentials(null, null);
        _loginInfo = new DfLoginInfo(credentials.getUserName(),
                credentials.getPassword());
        PersistenceManagerFactory pmf = JDOHelper
                .getPersistenceManagerFactory("JDOTesting");
        _pm = pmf.getPersistenceManager(getLoginInfo().getUser(),
                getLoginInfo().getPassword());
        Transaction tr = _pm.currentTransaction();
        tr.begin();
        JDOConnection connection = _pm.getDataStoreConnection();
        // we need to create session within
        // existing transaction and use it
        _underneathSession = ((IDfSession) connection.getNativeConnection());
        connection.close();
    }

    public IDfLoginInfo getLoginInfo() {
        return _loginInfo;
    }

    protected String getLoginName() {
        return _loginInfo.getUser();
    }

    @After
    public void tearDown() throws Exception {
        Transaction tr = _pm.currentTransaction();
        tr.rollback();
        _pm.close();
    }

    protected PersistenceManager getPersistenceManager() {
        return _pm;
    }

    protected IDfSession getUnderneathSession() {
        return _underneathSession;
    }

}
