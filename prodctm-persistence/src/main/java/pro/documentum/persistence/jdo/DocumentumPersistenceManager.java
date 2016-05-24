package pro.documentum.persistence.jdo;

import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;

import pro.documentum.persistence.common.IDocumentumCredentialsHolder;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DocumentumPersistenceManager extends JDOPersistenceManager
        implements IDocumentumCredentialsHolder {

    public static final String OPTION_LOGININFO = "loginInfo";

    private final String _userName;

    private final String _password;

    public DocumentumPersistenceManager(final JDOPersistenceManagerFactory pmf,
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
