package pro.documentum.persistence.common.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.query.compiler.QueryCompilation;
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
public class DQLQueryHelper<T extends Query & IDocumentumQuery> {

    private final T _query;

    public DQLQueryHelper(final T query) {
        _query = query;
    }

    public void compileQueryFull(final QueryCompilation queryCompilation,
            final DQLQueryCompilation datastoreCompilcation,
            final Map<?, ?> parameters,
            final Map<String, Query.SubqueryDefinition> subqueries,
            final AbstractClassMetaData candidateCmd) {
        DQLMapper mapper = new DQLMapper(queryCompilation, parameters,
                subqueries, candidateCmd, _query.getExecutionContext(), _query);
        mapper.compile(datastoreCompilcation);
    }

    public Object performExecute(final QueryCompilation compilation,
            final DQLQueryCompilation datastoreCompilation, final Map parameters) {
        ExecutionContext context = _query.getExecutionContext();
        StoreManager storeManager = _query.getStoreManager();
        ClassLoaderResolver classLoaderResolver = context
                .getClassLoaderResolver();
        MetaDataManager metaDataManager = context.getMetaDataManager();

        ManagedConnection mconn = storeManager.getConnection(context);
        try {
            IDfSession session = (IDfSession) mconn.getConnection();

            long startTime = System.currentTimeMillis();
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(Localiser.msg("021046",
                        _query.getLanguage(), _query.getSingleStringQuery(),
                        null));
            }

            List candidates = null;
            boolean filterInMemory = _query.getFilter() != null;
            boolean resultInMemory = _query.getResult() != null;
            boolean orderInMemory = _query.getOrdering() != null;
            boolean rangeInMemory = _query.getRange() != null;
            if (_query.getCandidateCollection() != null) {
                candidates = new ArrayList(_query.getCandidateCollection());
            } else if (_query.evaluateInMemory()) {
                AbstractClassMetaData cmd = metaDataManager
                        .getMetaDataForClass(_query.getCandidateClass(),
                                classLoaderResolver);
                String dqlTextForQuery = DNQueries.getDqlTextForQuery(context,
                        cmd, compilation.getCandidateAlias(),
                        _query.isSubclasses(), null, null, null, null, null);
                candidates = DNQueries.executeDqlQuery(_query, session,
                        dqlTextForQuery, cmd);
            } else {
                filterInMemory = !datastoreCompilation.isFilterComplete();
                if (!filterInMemory) {
                    resultInMemory = !datastoreCompilation.isResultComplete();
                    orderInMemory = !datastoreCompilation.isOrderComplete();
                    if (!orderInMemory) {
                        rangeInMemory = !datastoreCompilation.isRangeComplete();
                    }
                }
                AbstractClassMetaData cmd = metaDataManager
                        .getMetaDataForClass(_query.getCandidateClass(),
                                classLoaderResolver);
                String dqlText = datastoreCompilation.getDqlText();
                candidates = DNQueries.executeDqlQuery(_query, session,
                        dqlText, cmd);
            }

            Collection results = candidates;
            if (filterInMemory || resultInMemory || rangeInMemory
                    || _query.getResultClass() != null || orderInMemory) {
                if (results instanceof QueryResult) {
                    // Make sure the cursor(s) are all loaded
                    ((QueryResult) results).disconnect();
                }

                // Evaluate result/filter/grouping/having/ordering in-memory
                JavaQueryEvaluator resultMapper = new JDOQLEvaluator(_query,
                        results, compilation, parameters, classLoaderResolver);
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
            final Collection<?> results) {
        if (!(results instanceof QueryResult<?>)) {
            return results;
        }
        QueryResult<?> queryResult = (QueryResult<?>) results;
        ManagedConnectionResourceListener listener = new QueryResultResourceListener(
                _query, queryResult, mconn);
        mconn.addListener(listener);
        if (queryResult instanceof AbstractQueryResult) {
            ((AbstractQueryResult) queryResult).addConnectionListener(listener);
        }
        return results;
    }

}
