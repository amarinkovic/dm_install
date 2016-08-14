package pro.documentum.persistence.jdo;

import java.util.HashMap;
import java.util.Map;

import javax.jdo.JDODataStoreException;
import javax.jdo.JDOException;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.datastore.JDOConnection;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.documentum.fc.client.IDfSession;

import pro.documentum.persistence.common.connection.SessionManagerPoolFactory;
import pro.documentum.util.sessions.Sessions;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class ConnectionFactoryImplTest extends JDOTestSupport {

    private Map<String, String> getProps() throws Exception {
        Map<String, String> properties = new HashMap<>();
        properties.put("javax.jdo.PersistenceManagerFactoryClass",
                PersistenceManagerFactoryImpl.class.getName());
        properties.put("javax.jdo.option.ConnectionURL", "dctm:"
                + getUnderneathSession().getDocbaseName());
        return properties;
    }

    @Test(expected = JDODataStoreException.class)
    public void testWrongLogin() throws Exception {
        PersistenceManagerFactory pmf = JDOHelper
                .getPersistenceManagerFactory("JDOTesting");
        PersistenceManager pm1 = pmf.getPersistenceManager(
                RandomStringUtils.randomAlphabetic(100), null);
        pm1.getDataStoreConnection().getNativeConnection();
    }

    @Test(expected = JDOException.class)
    public void testPool() throws Exception {
        Map<String, String> properties = getProps();
        properties.put(SessionManagerPoolFactory.MAX_POOL_SIZE, "2");
        properties.put(SessionManagerPoolFactory.MAX_WAIT_TIME, "5");
        PersistenceManagerFactory pmf = JDOHelper
                .getPersistenceManagerFactory(properties);
        PersistenceManager pm1 = pmf.getPersistenceManager(getLoginInfo()
                .getUser(), getLoginInfo().getPassword());
        pm1.getDataStoreConnection();
        pm1.getDataStoreConnection();
        pm1.getDataStoreConnection();
    }

    @Test
    public void testNoSharedSessions1() throws Exception {
        PersistenceManagerFactory pmf = JDOHelper
                .getPersistenceManagerFactory(getProps());
        PersistenceManager pm1 = pmf.getPersistenceManager(getLoginInfo()
                .getUser(), getLoginInfo().getPassword());
        pm1.getDataStoreConnection().getNativeConnection();
        pm1.currentTransaction().begin();
        PersistenceManager pm2 = pmf.getPersistenceManager(getLoginInfo()
                .getUser(), getLoginInfo().getPassword());
        pm2.currentTransaction().begin();
        JDOConnection c1 = pm1.getDataStoreConnection();
        JDOConnection c2 = pm2.getDataStoreConnection();

        IDfSession s11 = (IDfSession) c1.getNativeConnection();
        IDfSession s12 = (IDfSession) c1.getNativeConnection();
        IDfSession s21 = (IDfSession) c2.getNativeConnection();

        assertEquals(Sessions.getSessionId(s11), Sessions.getSessionId(s12));
        assertNotEquals(Sessions.getSessionId(s11), Sessions.getSessionId(s21));

        c1.close();
        c2.close();
        pm1.currentTransaction().rollback();
        pm1.close();
        pm2.currentTransaction().rollback();
        pm2.close();

        assertFalse(s11.isConnected());
        assertFalse(s12.isConnected());
        assertFalse(s21.isConnected());
    }

    @Test
    public void testNoSharedSessions2() throws Exception {
        PersistenceManagerFactory pmf = JDOHelper
                .getPersistenceManagerFactory(getProps());
        PersistenceManager pm1 = pmf.getPersistenceManager(getLoginInfo()
                .getUser(), getLoginInfo().getPassword());
        pm1.currentTransaction().begin();
        PersistenceManager pm2 = pmf.getPersistenceManager(getLoginInfo()
                .getUser(), getLoginInfo().getPassword());
        pm2.currentTransaction().begin();
        JDOConnection c1 = pm1.getDataStoreConnection();
        IDfSession s11 = (IDfSession) c1.getNativeConnection();
        c1.close();
        c1 = pm1.getDataStoreConnection();
        JDOConnection c2 = pm2.getDataStoreConnection();

        IDfSession s12 = (IDfSession) c1.getNativeConnection();
        IDfSession s21 = (IDfSession) c2.getNativeConnection();

        assertEquals(Sessions.getSessionId(s11), Sessions.getSessionId(s12));
        assertNotEquals(Sessions.getSessionId(s11), Sessions.getSessionId(s21));

        c1.close();
        c2.close();
        pm1.currentTransaction().rollback();
        pm1.close();
        pm2.currentTransaction().rollback();
        pm2.close();

        assertFalse(s11.isConnected());
        assertFalse(s12.isConnected());
        assertFalse(s21.isConnected());
    }

}
