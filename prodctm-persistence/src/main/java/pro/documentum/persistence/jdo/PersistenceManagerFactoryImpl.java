package pro.documentum.persistence.jdo;

import java.util.Map;

import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.metadata.PersistenceUnitMetaData;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PersistenceManagerFactoryImpl extends JDOPersistenceManagerFactory {

    private static final long serialVersionUID = 1947522885236246186L;

    public PersistenceManagerFactoryImpl() {
        super();
    }

    public PersistenceManagerFactoryImpl(final PersistenceUnitMetaData pumd,
            final Map<?, ?> overrideProps) {
        super(pumd, overrideProps);
    }

    public PersistenceManagerFactoryImpl(final Map<?, ?> props) {
        super(props);
    }

    @Override
    protected JDOPersistenceManager newPM(
            final JDOPersistenceManagerFactory jdoPmf, final String userName,
            final String password) {
        return new PersistenceManagerImpl(jdoPmf, userName, password);
    }

}
