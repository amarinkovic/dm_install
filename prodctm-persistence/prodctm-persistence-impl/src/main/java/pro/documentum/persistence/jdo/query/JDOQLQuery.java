package pro.documentum.persistence.jdo.query;

import java.util.Collection;
import java.util.Map;

import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.query.AbstractJDOQLQuery;
import org.datanucleus.store.query.QueryManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.util.NucleusLogger;

import pro.documentum.persistence.common.query.DQLQueryCompilation;
import pro.documentum.persistence.common.query.DQLQueryHelper;
import pro.documentum.persistence.common.query.IDocumentumQuery;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class JDOQLQuery<R> extends AbstractJDOQLQuery implements
        IDocumentumQuery<R> {

    private static final long serialVersionUID = 7280457567657230093L;
    private final DQLQueryHelper<R, ?> _queryHelper;
    private transient DQLQueryCompilation _datastoreCompilation;

    @SuppressWarnings({"unchecked", "rawtypes" })
    public JDOQLQuery(final StoreManager storeMgr, final ExecutionContext ec) {
        super(storeMgr, ec);
        _queryHelper = new DQLQueryHelper(this);
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    public JDOQLQuery(final StoreManager storeMgr, final ExecutionContext ec,
            final AbstractJDOQLQuery q) {
        super(storeMgr, ec, q);
        _queryHelper = new DQLQueryHelper(this);
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    public JDOQLQuery(final StoreManager storeMgr, final ExecutionContext ec,
            final String query) {
        super(storeMgr, ec, query);
        _queryHelper = new DQLQueryHelper(this);
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
        if (!isPrecompilable()) {
            NucleusLogger.GENERAL
                    .info("Query compiled but not precompilable so ditching datastore compilation");
            _datastoreCompilation = null;
            return false;
        }
        return true;
    }

    private boolean isPrecompilable() {
        return _datastoreCompilation.isPrecompilable();
    }

    @Override
    public boolean evaluateInMemory() {
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

    @Override
    @SuppressWarnings("rawtypes")
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
        if (!inMemory) {
            _queryHelper.compileQueryFull(parameterValues);
        }

        if (cacheKey == null) {
            return;
        }

        if (isPrecompilable()) {
            qm.addDatastoreQueryCompilation(datastoreKey, getLanguage(),
                    cacheKey, _datastoreCompilation);
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes" })
    protected Object performExecute(final Map parameters) {
        return _queryHelper.performExecute(parameters);
    }

    @Override
    public String getNativeQuery() {
        if (_datastoreCompilation != null) {
            return _datastoreCompilation.getDqlText();
        }
        return null;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void checkParameterTypesAgainstCompilation(
            final Map parameterValues) {
        // todo perform checks
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<R> getCandidateCollection() {
        return candidateCollection;
    }

    @Override
    public AbstractClassMetaData getCandidateMetaData() {
        return super.getCandidateClassMetaData();
    }

    @Override
    public String getCandidateAlias() {
        return compilation.getCandidateAlias();
    }

    @Override
    public DQLQueryCompilation getDatastoreCompilation() {
        return _datastoreCompilation;
    }

    @Override
    public DQLQueryHelper<R, ?> getQueryHelper() {
        return _queryHelper;
    }

}
