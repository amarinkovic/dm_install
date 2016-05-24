package pro.documentum.persistence.jdo;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import javax.jdo.datastore.JDOConnection;

import com.documentum.fc.client.IDfSession;

import pro.documentum.junit.DfcTestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class JDOTestSupport extends DfcTestSupport {

    private PersistenceManager _pm;

    private IDfSession _session;

    @Override
    protected void doPostSetup() throws Exception {
        PersistenceManagerFactory pmf = JDOHelper
                .getPersistenceManagerFactory("JDOTesting");
        _pm = pmf.getPersistenceManager(getLoginInfo().getUser(),
                getLoginInfo().getPassword());
        Transaction tr = _pm.currentTransaction();
        tr.begin();
        JDOConnection connection = _pm.getDataStoreConnection();
        // we need to create session within
        // existing transaction and use it
        _session = ((IDfSession) connection.getNativeConnection());
        connection.close();
    }

    @Override
    public void doPreTearDown() throws Exception {
        Transaction tr = _pm.currentTransaction();
        tr.rollback();
        _pm.close();
    }

    protected PersistenceManager getPersistenceManager() {
        return _pm;
    }

    @Override
    protected IDfSession getSession() {
        return _session;
    }

}
