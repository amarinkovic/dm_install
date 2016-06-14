package pro.documentum.persistence.jdo;

import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;

import pro.documentum.persistence.common.ICredentialsHolder;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PersistenceManagerImpl extends JDOPersistenceManager implements
        ICredentialsHolder {

    private final String _userName;

    private final String _password;

    public PersistenceManagerImpl(final JDOPersistenceManagerFactory pmf,
            final String userName, final String password) {
        super(pmf, userName, password);
        _userName = userName;
        _password = password;
    }

    @Override
    public String getUserName() {
        return _userName;
    }

    @Override
    public String getPassword() {
        return _password;
    }

}
