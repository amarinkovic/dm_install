package pro.documentum.util.sessions;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

import pro.documentum.util.IDfSessionInvoker;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class Sessions {

    private Sessions() {
        super();
    }

    public static boolean beginTransactionIfNotActive(final IDfSession session)
        throws DfException {
        boolean transactionActive = session.isTransactionActive();
        if (!transactionActive) {
            session.beginTrans();
        }
        return !transactionActive;
    }

    public static <T> T inTransaction(final IDfSession session,
            final IDfSessionInvoker<T> invoker) throws DfException {
        boolean txStartsHere = false;
        try {
            txStartsHere = beginTransactionIfNotActive(session);
            T result = invoker.invoke(session);
            if (txStartsHere) {
                session.commitTrans();
                txStartsHere = false;
            }
            return result;
        } finally {
            if (txStartsHere && session.isTransactionActive()) {
                session.abortTrans();
            }
        }
    }

    public static String getHostName(final IDfSession session)
        throws DfException {
        return session.getSessionConfig().getString("r_host_name");
    }

}
