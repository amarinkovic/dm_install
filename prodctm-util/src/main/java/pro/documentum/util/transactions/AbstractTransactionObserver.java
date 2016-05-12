package pro.documentum.util.transactions;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.transaction.IDfTransactionObserver;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractTransactionObserver implements
        IDfTransactionObserver {

    @Override
    public void onPreCommit(final IDfSession session) throws DfException {

    }

    @Override
    public void onPostCommit(final IDfSession session, final int xid)
        throws DfException {

    }

    @Override
    public void onPreRollback(final IDfSession session) throws DfException {

    }

    @Override
    public void onPostRollback(final IDfSession session, final int xid)
        throws DfException {

    }

}
