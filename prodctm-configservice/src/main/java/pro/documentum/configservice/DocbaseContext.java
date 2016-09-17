package pro.documentum.configservice;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.impl.session.ISession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfRuntimeException;
import com.documentum.services.config.IDocbaseContext;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
final class DocbaseContext implements IDocbaseContext {

    private final IConfigProvider _configProvider;

    private DocbaseContext(final IConfigProvider configProvider) {
        _configProvider = configProvider;
    }

    static DocbaseContext getInstance(final IConfigProvider configProvider) {
        return new DocbaseContext(configProvider);
    }

    @Override
    public IDfSession getDfSession(final String docbase) {
        String currentDocbase = getCurrentDocbaseName();
        if (docbase.equals(currentDocbase)) {
            return ((ISession) getCurrentDfSession()).newStrongHandle();
        }
        return null;
    }

    @Override
    public IDfSession getCurrentDfSession() {
        return _configProvider.getSession();
    }

    @Override
    public String getCurrentDocbaseName() {
        try {
            if (getCurrentDfSession() == null) {
                return null;
            }
            return getCurrentDfSession().getDocbaseName();
        } catch (DfException e) {
            throw DfRuntimeException.convertToRuntimeException(e);
        }
    }

    @Override
    public String getCurrentUserName() {
        IDfSession session = getCurrentDfSession();
        if (session != null) {
            return session.getSessionManager()
                    .getIdentity(getCurrentDocbaseName()).getUser();
        }
        return null;
    }

    @Override
    public boolean isCurrentDocbaseConnected() {
        return getCurrentDfSession() != null;
    }

}
