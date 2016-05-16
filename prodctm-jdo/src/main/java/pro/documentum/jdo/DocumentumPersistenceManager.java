package pro.documentum.jdo;

import org.datanucleus.ExecutionContext;
import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;

import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DocumentumPersistenceManager extends JDOPersistenceManager {

    public static final String OPTION_LOGININFO = "loginInfo";

    private final String _userName;

    private final String _password;

    public DocumentumPersistenceManager(final JDOPersistenceManagerFactory pmf,
            final String userName, final String password) {
        super(pmf, userName, password);
        _userName = userName;
        _password = password;
    }

    private String getUserName() {
        return _userName;
    }

    private String getPassword() {
        return _password;
    }

    static IDfLoginInfo getLoginInfo(final ExecutionContext executionContext) {
        if (executionContext == null) {
            return null;
        }
        Object owner = executionContext.getOwner();
        if (!(owner instanceof DocumentumPersistenceManager)) {
            return null;
        }
        DocumentumPersistenceManager pm = (DocumentumPersistenceManager) owner;
        return new DfLoginInfo(pm.getUserName(), pm.getPassword());
    }

}
