package pro.documentum.persistence.common.query.result;

import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.connection.ManagedConnectionResourceListener;
import org.datanucleus.store.query.QueryResult;

import pro.documentum.persistence.common.query.IDocumentumQuery;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class QueryResultResourceListener implements
        ManagedConnectionResourceListener {

    private final IDocumentumQuery _query;

    private final QueryResult<?> _queryResult;

    private final ManagedConnection _mconn;

    public QueryResultResourceListener(final IDocumentumQuery query,
            final QueryResult<?> queryResult, final ManagedConnection mconn) {
        _query = query;
        _queryResult = queryResult;
        _mconn = mconn;
    }

    public void transactionFlushed() {
        // noop
    }

    public void transactionPreClose() {
        // todo: need to figure out whether
        // we need to fetch all results or no
        _queryResult.disconnect();
    }

    public void managedConnectionPreClose() {
        if (_query.getExecutionContext().getTransaction().isActive()) {
            return;
        }
        _queryResult.disconnect();
    }

    public void managedConnectionPostClose() {
        // noop
    }

    public void resourcePostClose() {
        _mconn.removeListener(this);
    }

}
