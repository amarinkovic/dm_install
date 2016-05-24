package pro.documentum.persistence.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import pro.documentum.junit.DfcTestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class JPATestSupport extends DfcTestSupport {

    private EntityManager _em;

    @Override
    protected void doPostSetup() throws Exception {
        EntityManagerFactory emf = Persistence
                .createEntityManagerFactory("JPATesting");
        _em = emf.createEntityManager();
        EntityTransaction tr = _em.getTransaction();
        tr.begin();
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

}
