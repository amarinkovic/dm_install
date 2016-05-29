package pro.documentum.util.sessions;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.impl.session.ISession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;

import pro.documentum.util.IDfSessionInvoker;
import pro.documentum.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class Sessions {

    public static final IDfClientX CLIENT_X = new DfClientX();

    public static final IDfClient CLIENT;

    static {
        try {
            CLIENT = CLIENT_X.getLocalClient();
        } catch (DfException ex) {
            throw new RuntimeException(ex);
        }
    }

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

    public static String getSessionId(final IDfSession session) {
        try {
            return session.getSessionConfig().getString("session_id");
        } catch (DfException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getLoginUserName(final IDfSession session) {
        try {
            return session.getLoginUserName();
        } catch (DfException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getDocbaseName(final IDfSession session) {
        try {
            return session.getDocbaseName();
        } catch (DfException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static boolean disableServerTimeout(final IDfSession dfSession)
        throws DfException {
        return disableServerTimeout(dfSession, false);
    }

    public static boolean disableServerTimeout(final IDfSession dfSession,
            final boolean ignoreex) throws DfException {
        try {
            ISession session = (ISession) dfSession;
            return session.getDocbaseApi().disableTimeout();
        } catch (DfException ex) {
            if (ignoreex) {
                Logger.error(ex);
                return false;
            }
            throw ex;
        }
    }

    public static boolean enableServerTimeout(final IDfSession dfSession)
        throws DfException {
        return enableServerTimeout(dfSession, true);
    }

    public static boolean enableServerTimeout(final IDfSession dfSession,
            final boolean ignoreex) throws DfException {
        try {
            ISession session = (ISession) dfSession;
            return session.getDocbaseApi().enableTimeout();
        } catch (DfException ex) {
            if (ignoreex) {
                Logger.error(ex);
                return false;
            }
            throw ex;
        }
    }

    public static IDfSessionManager newSessionManager() throws DfException {
        return CLIENT.newSessionManager();
    }

    public static IDfSessionManager newSessionManager(
            final IDfLoginInfo loginInfo, final String docbase)
        throws DfException {
        loginInfo.setPeriodicAuthentication(false);
        loginInfo.setForceAuthentication(false);
        IDfSessionManager sessionManager = newSessionManager();
        sessionManager.setIdentity(docbase, loginInfo);
        return sessionManager;
    }

    public static IDfSessionManager newSessionManager(
            final IDfSessionManager manager, final String docbase)
        throws DfException {
        return newSessionManager(new DfLoginInfo(manager.getIdentity(docbase)),
                docbase);
    }

    public static IDfSession brandNew(final IDfSessionManager manager,
            final String docbase) throws DfException {
        return newSessionManager(manager, docbase).getSession(docbase);
    }

    public static IDfSession brandNew(final IDfLoginInfo loginInfo,
            final String docbase) throws DfException {
        return newSessionManager(new DfLoginInfo(loginInfo), docbase)
                .getSession(docbase);
    }

    public static void release(final IDfSession session) {
        if (session == null) {
            return;
        }
        IDfSessionManager sessionManager = session.getSessionManager();
        if (sessionManager != null) {
            sessionManager.release(session);
            return;
        }
        try {
            session.disconnect();
        } catch (DfException ex) {
            Logger.error(ex);
        }
    }

    public static IDfClient getClient() {
        return CLIENT;
    }

    public static IDfClientX getClientX() {
        return CLIENT_X;
    }

}
