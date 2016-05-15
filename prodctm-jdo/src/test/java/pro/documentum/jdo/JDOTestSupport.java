package pro.documentum.jdo;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import javax.jdo.datastore.JDOConnection;

import org.junit.After;
import org.junit.Before;

import com.documentum.fc.client.IDfSession;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class JDOTestSupport {

    private PersistenceManager _pm;

    private IDfSession _session;

    @Before
    public void setUp() throws Exception {
        PersistenceManagerFactory pmf = JDOHelper
                .getPersistenceManagerFactory("Testing");
        _pm = pmf.getPersistenceManager();
        Transaction tr = _pm.currentTransaction();
        tr.begin();
        JDOConnection connection = _pm.getDataStoreConnection();
        _session = ((IDfSession) connection.getNativeConnection());
        connection.close();
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

    protected IDfSession getSession() {
        return _session;
    }

}
