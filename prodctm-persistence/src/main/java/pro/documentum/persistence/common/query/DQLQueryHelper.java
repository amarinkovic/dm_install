package pro.documentum.persistence.common.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.query.evaluator.JDOQLEvaluator;
import org.datanucleus.query.evaluator.JavaQueryEvaluator;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.connection.ManagedConnectionResourceListener;
import org.datanucleus.store.query.AbstractQueryResult;
import org.datanucleus.store.query.Query;
import org.datanucleus.store.query.QueryResult;
import org.datanucleus.util.Localiser;
import org.datanucleus.util.NucleusLogger;

import com.documentum.fc.client.IDfSession;

import pro.documentum.persistence.common.query.result.QueryResultResourceListener;
import pro.documentum.persistence.common.util.DNQueries;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLQueryHelper<R, T extends Query<?> & IDocumentumQuery<R>> {

    private final T _query;

    public DQLQueryHelper(final T query) {
        _query = query;
    }

    public void compileQueryFull(final Map<?, ?> parameters) {
        DQLMapper<R, T> mapper = new DQLMapper<R, T>(_query, parameters);
        mapper.compile(_query.getDatastoreCompilation());
    }

    @SuppressWarnings("unchecked")
    public Object performExecute(final Map<?, ?> parameters) {
        ExecutionContext context = _query.getExecutionContext();
        StoreManager storeManager = _query.getStoreManager();
        ClassLoaderResolver classLoaderResolver = context
                .getClassLoaderResolver();
        ManagedConnection mconn = storeManager.getConnection(context);
        try {
            IDfSession session = (IDfSession) mconn.getConnection();

            long startTime = System.currentTimeMillis();
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(Localiser.msg("021046",
                        _query.getLanguage(), _query.getSingleStringQuery(),
                        null));
            }

            List<R> candidates = null;
            boolean filterInMemory = _query.getFilter() != null;
            boolean resultInMemory = _query.getResult() != null;
            boolean orderInMemory = _query.getOrdering() != null;
            boolean rangeInMemory = _query.getRange() != null;
            if (_query.getCandidateCollection() != null) {
                candidates = new ArrayList<>(_query.getCandidateCollection());
            } else if (_query.evaluateInMemory()) {
                String dqlTextForQuery = DNQueries.getDqlTextForQuery(_query,
                        null, null, null, null, null);
                candidates = DNQueries.executeDqlQuery(_query, session,
                        dqlTextForQuery);
            } else {
                filterInMemory = !isFilterComplete();
                if (!filterInMemory) {
                    resultInMemory = !isResultComplete();
                    orderInMemory = !isOrderComplete();
                    if (!orderInMemory) {
                        rangeInMemory = !isRangeComplete();
                    }
                }
                candidates = DNQueries.executeDqlQuery(_query, session,
                        getDqlText());
            }

            Collection<R> results = candidates;
            if (filterInMemory || resultInMemory || rangeInMemory
                    || _query.getResultClass() != null || orderInMemory) {
                if (results instanceof QueryResult) {
                    // Make sure the cursor(s) are all loaded
                    ((QueryResult) results).disconnect();
                }

                // Evaluate result/filter/grouping/having/ordering in-memory
                JavaQueryEvaluator resultMapper = new JDOQLEvaluator(_query,
                        results, _query.getCompilation(), parameters,
                        classLoaderResolver);
                results = resultMapper.execute(filterInMemory, orderInMemory,
                        resultInMemory, true, rangeInMemory);
            }

            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(Localiser.msg("021074",
                        _query.getLanguage(), ""
                                + (System.currentTimeMillis() - startTime)));
            }

            return addListeners(mconn, results);
        } finally {
            mconn.release();
        }
    }

    private Object addListeners(final ManagedConnection mconn,
            final Collection<R> results) {
        if (!(results instanceof QueryResult)) {
            return results;
        }
        QueryResult<R> queryResult = (QueryResult<R>) results;
        ManagedConnectionResourceListener listener = new QueryResultResourceListener<R, T>(
                _query, queryResult, mconn);
        mconn.addListener(listener);
        if (queryResult instanceof AbstractQueryResult) {
            ((AbstractQueryResult) queryResult).addConnectionListener(listener);
        }
        return results;
    }

    private boolean isFilterComplete() {
        return _query.getDatastoreCompilation().isFilterComplete();
    }

    private boolean isResultComplete() {
        return _query.getDatastoreCompilation().isResultComplete();
    }

    private boolean isRangeComplete() {
        return _query.getDatastoreCompilation().isRangeComplete();
    }

    private boolean isOrderComplete() {
        return _query.getDatastoreCompilation().isOrderComplete();
    }

    private String getDqlText() {
        return _query.getDatastoreCompilation().getDqlText();
    }

}
