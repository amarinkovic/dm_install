package pro.documentum.persistence.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.datanucleus.PropertyNames;
import org.datanucleus.store.NucleusConnection;

import com.documentum.fc.client.IDfSession;

import pro.documentum.junit.DfcTestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class JPATestSupport extends DfcTestSupport {

    private EntityManagerFactory _emf;

    private EntityManager _em;

    private IDfSession _underneathSession;

    @Override
    protected void doPostSetup() throws Exception {
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

    @Override
    public void doPreTearDown() throws Exception {
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
