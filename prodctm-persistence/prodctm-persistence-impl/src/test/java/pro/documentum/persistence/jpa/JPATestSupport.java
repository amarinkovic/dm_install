package pro.documentum.persistence.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.datanucleus.PropertyNames;
import org.datanucleus.store.NucleusConnection;
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
public abstract class JPATestSupport extends Assert {

    private EntityManagerFactory _emf;

    private EntityManager _em;

    private IDfSession _underneathSession;

    private IDfLoginInfo _loginInfo;

    @Before
    public void setUp() throws Exception {
        IDocumentumCredentials credentials = new PropertiesCredentialManager(
                null).getCredentials(null, null);
        _loginInfo = new DfLoginInfo(credentials.getUserName(),
                credentials.getPassword());
        _emf = Persistence.createEntityManagerFactory("JPATesting");
        Map<String, Object> properties = new HashMap<>();
        properties.put(PropertyNames.PROPERTY_CONNECTION_USER_NAME,
                getLoginInfo().getUser());
        properties.put(PropertyNames.PROPERTY_CONNECTION_PASSWORD,
                getLoginInfo().getPassword());
        _em = _emf.createEntityManager(properties);
        EntityTransaction tr = _em.getTransaction();
        tr.begin();
        NucleusConnection connection = _em.unwrap(NucleusConnection.class);
        _underneathSession = (IDfSession) connection.getNativeConnection();
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
        EntityTransaction tr = _em.getTransaction();
        tr.rollback();
        _em.close();
    }

    protected EntityManager getEntityManager() {
        return _em;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return _emf;
    }

    protected IDfSession getUnderneathSession() {
        return _underneathSession;
    }

}
