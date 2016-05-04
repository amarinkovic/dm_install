package pro.documentum.jdo.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.query.evaluator.JDOQLEvaluator;
import org.datanucleus.query.evaluator.JavaQueryEvaluator;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.connection.ManagedConnectionResourceListener;
import org.datanucleus.store.query.AbstractJDOQLQuery;
import org.datanucleus.store.query.AbstractQueryResult;
import org.datanucleus.store.query.QueryManager;
import org.datanucleus.store.query.QueryResult;
import org.datanucleus.util.Localiser;
import org.datanucleus.util.NucleusLogger;

import com.documentum.fc.client.IDfSession;

import pro.documentum.jdo.util.DNQueries;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class JDOQLQuery extends AbstractJDOQLQuery {

    private transient DQLQueryCompilation _datastoreCompilation;

    public JDOQLQuery(final StoreManager storeMgr, final ExecutionContext ec) {
        super(storeMgr, ec);
    }

    public JDOQLQuery(final StoreManager storeMgr, final ExecutionContext ec,
            final AbstractJDOQLQuery q) {
        super(storeMgr, ec, q);
    }

    public JDOQLQuery(final StoreManager storeMgr, final ExecutionContext ec,
            final String query) {
        super(storeMgr, ec, query);
    }

    protected void discardCompiled() {
        super.discardCompiled();
        _datastoreCompilation = null;
    }

    protected boolean isCompiled() {
        if (evaluateInMemory()) {
            return compilation != null;
        }

        if (compilation == null || _datastoreCompilation == null) {
            return false;
        }
        if (!_datastoreCompilation.isPrecompilable()) {
            NucleusLogger.GENERAL
                    .info("Query compiled but not precompilable so ditching datastore compilation");
            _datastoreCompilation = null;
            return false;
        }
        return true;
    }

    protected boolean evaluateInMemory() {
        if (candidateCollection == null) {
            return super.evaluateInMemory();
        }
        if (compilation != null && compilation.getSubqueryAliases() != null) {
            NucleusLogger.QUERY
                    .warn("In-memory evaluator doesn't currently handle subqueries "
                            + "completely so evaluating in datastore");
            return false;
        }
        return getBooleanExtensionProperty(EXTENSION_EVALUATE_IN_MEMORY, false);
    }

    protected synchronized void compileInternal(final Map parameterValues) {
        if (isCompiled()) {
            return;
        }

        super.compileInternal(parameterValues);
        boolean inMemory = evaluateInMemory();
        if (candidateCollection != null && inMemory) {
            return;
        }

        if (candidateClass == null) {
            throw new NucleusUserException(Localiser.msg("021009",
                    candidateClassName));
        }

        ec.hasPersistenceInformationForClass(candidateClass);

        AbstractClassMetaData cmd = getCandidateClassMetaData();

        QueryManager qm = getQueryManager();
        String datastoreKey = getStoreManager().getQueryCacheKey();
        String cacheKey = getQueryCacheKey();
        if (useCaching()) {
            _datastoreCompilation = (DQLQueryCompilation) qm
                    .getDatastoreQueryCompilation(datastoreKey, getLanguage(),
                            cacheKey);
            if (_datastoreCompilation != null) {
                return;
            }
        }

        _datastoreCompilation = new DQLQueryCompilation();
        synchronized (_datastoreCompilation) {
            if (!inMemory) {
                compileQueryFull(parameterValues, cmd);
            }
        }

        if (cacheKey != null) {
            if (_datastoreCompilation.isPrecompilable()) {
                qm.addDatastoreQueryCompilation(datastoreKey, getLanguage(),
                        cacheKey, _datastoreCompilation);
            }
        }
    }

    protected Object performExecute(final Map parameters) {
        ManagedConnection mconn = getStoreManager().getConnection(ec);
        try {
            IDfSession session = (IDfSession) mconn.getConnection();

            long startTime = System.currentTimeMillis();
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(Localiser.msg("021046", "JDOQL",
                        getSingleStringQuery(), null));
            }

            List candidates = null;
            boolean filterInMemory = filter != null;
            boolean resultInMemory = result != null;
            boolean orderInMemory = ordering != null;
            boolean rangeInMemory = range != null;
            if (candidateCollection != null) {
                candidates = new ArrayList(candidateCollection);
            } else if (evaluateInMemory()) {
                AbstractClassMetaData cmd = ec.getMetaDataManager()
                        .getMetaDataForClass(candidateClass,
                                ec.getClassLoaderResolver());
                String dqlTextForQuery = DNQueries.getDqlTextForQuery(ec, cmd,
                        compilation.getCandidateAlias(), subclasses, null,
                        null, null, null, null);
                candidates = DNQueries.executeDqlQuery(this, session,
                        dqlTextForQuery, cmd);
            } else {
                filterInMemory = !_datastoreCompilation.isFilterComplete();
                if (!filterInMemory) {
                    resultInMemory = !_datastoreCompilation.isResultComplete();
                    orderInMemory = !_datastoreCompilation.isOrderComplete();
                    if (!orderInMemory) {
                        rangeInMemory = !_datastoreCompilation
                                .isRangeComplete();
                    }
                }
                AbstractClassMetaData cmd = ec.getMetaDataManager()
                        .getMetaDataForClass(candidateClass,
                                ec.getClassLoaderResolver());
                String dqlText = _datastoreCompilation.getDqlText();
                candidates = DNQueries.executeDqlQuery(this, session, dqlText,
                        cmd);
            }

            Collection results = candidates;
            if (filterInMemory || resultInMemory || rangeInMemory
                    || resultClass != null || orderInMemory) {
                if (results instanceof QueryResult) {
                    // Make sure the cursor(s) are all loaded
                    ((QueryResult) results).disconnect();
                }

                // Evaluate result/filter/grouping/having/ordering in-memory
                JavaQueryEvaluator resultMapper = new JDOQLEvaluator(this,
                        results, compilation, parameters, ec
                                .getClassLoaderResolver());
                results = resultMapper.execute(filterInMemory, orderInMemory,
                        resultInMemory, true, rangeInMemory);
            }

            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(Localiser.msg("021074", "JDOQL", ""
                        + (System.currentTimeMillis() - startTime)));
            }

            if (results instanceof QueryResult) {
                final QueryResult qr1 = (QueryResult) results;
                final ManagedConnection mconn1 = mconn;
                ManagedConnectionResourceListener listener = new ManagedConnectionResourceListener() {
                    public void transactionFlushed() {
                    }

                    public void transactionPreClose() {
                        // Tx : disconnect query from ManagedConnection (read in unread rows etc)
                        qr1.disconnect();
                    }

                    public void managedConnectionPreClose() {
                        if (!ec.getTransaction().isActive()) {
                            // Non-Tx : disconnect query from ManagedConnection (read in unread rows etc)
                            qr1.disconnect();
                        }
                    }

                    public void managedConnectionPostClose() {
                    }

                    public void resourcePostClose() {
                        mconn1.removeListener(this);
                    }
                };
                mconn.addListener(listener);
                if (qr1 instanceof AbstractQueryResult) {
                    ((AbstractQueryResult) qr1).addConnectionListener(listener);
                }
            }

            return results;
        } finally {
            mconn.release();
        }
    }

    private void compileQueryFull(final Map parameters,
            final AbstractClassMetaData candidateCmd) {
        QueryToDQLMapper mapper = new QueryToDQLMapper(compilation, parameters,
                candidateCmd, ec, this);
        mapper.compile(_datastoreCompilation);
    }

    @Override
    public Object getNativeQuery() {
        if (_datastoreCompilation != null) {
            return _datastoreCompilation.getDqlText();
        }
        return null;
    }

}
