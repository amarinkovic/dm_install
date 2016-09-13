package pro.documentum.ext.isis;

import javax.jdo.PersistenceManager;

import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.runtime.system.persistence.PersistenceSession;

import pro.documentum.util.auth.ICredentials;
import pro.documentum.util.java.decorators.BaseDecorator;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PersistenceSessionDecorator extends
        BaseDecorator<PersistenceSession> {

    private final AuthenticationSession _authenticationSession;

    public PersistenceSessionDecorator(
            final PersistenceSession persistenceSession,
            final AuthenticationSession authenticationSession) {
        super(persistenceSession);
        _authenticationSession = authenticationSession;
    }

    public void open() {
        PersistenceSession ps = unwrap();
        ps.open();
        PersistenceManager pm = ps.getPersistenceManager();
        ICredentials in = of(_authenticationSession);
        ICredentials self = of(pm);
        if (self != null && in != null) {
            self.sync(in);
        }
    }

    protected ICredentials of(final AuthenticationSession session) {
        if (is(session)) {
            return as(session);
        }
        return null;
    }

    protected static ICredentials of(final PersistenceManager persistenceManager) {
        if (is(persistenceManager)) {
            return as(persistenceManager);
        }
        return null;
    }

    protected static boolean is(Object object) {
        return object instanceof ICredentials;
    }

    protected static ICredentials as(Object object) {
        return ICredentials.class.cast(object);
    }

}
