package pro.documentum.persistence.jdo;

import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;

import pro.documentum.persistence.common.ICredentialsHolder;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PersistenceManagerImpl extends JDOPersistenceManager implements
        ICredentialsHolder {

    public static final ThreadLocal<ICredentialsHolder> CREDENTIAL_HOLDER;

    static {
        CREDENTIAL_HOLDER = new ThreadLocal<>();
    }

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

    private <T> T withCredentials(final IInvoker<T> invoker) {
        boolean remove = putCredentials();
        try {
            return invoker.invoke();
        } finally {
            if (remove) {
                remove();
            }
        }
    }

    @Override
    public final Object newObjectIdInstance(final Class pcClass,
            final Object key) {
        return withCredentials(new IInvoker<Object>() {
            @Override
            public Object invoke() {
                return PersistenceManagerImpl.super.newObjectIdInstance(
                        pcClass, key);
            }
        });
    }

    private boolean putCredentials() {
        if (CREDENTIAL_HOLDER.get() != null) {
            return false;
        }
        CREDENTIAL_HOLDER.set(this);
        return true;
    }

    private void remove() {
        CREDENTIAL_HOLDER.remove();
    }

    public static ICredentialsHolder getCredentialHolder() {
        return CREDENTIAL_HOLDER.get();
    }

    private interface IInvoker<T> {

        T invoke();

    }

}
