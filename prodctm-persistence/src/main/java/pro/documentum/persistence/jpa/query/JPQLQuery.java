package pro.documentum.persistence.jpa.query;

import java.util.Collection;
import java.util.Map;

import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.query.AbstractJPQLQuery;
import org.datanucleus.store.query.QueryManager;
import org.datanucleus.util.NucleusLogger;

import pro.documentum.persistence.common.StoreManagerImpl;
import pro.documentum.persistence.common.query.DQLQueryCompilation;
import pro.documentum.persistence.common.query.DQLQueryHelper;
import pro.documentum.persistence.common.query.IDocumentumQuery;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class JPQLQuery<R> extends AbstractJPQLQuery implements
        IDocumentumQuery<R> {

    private static final long serialVersionUID = 8844314390625762269L;
    private final DQLQueryHelper<R, ?> _queryHelper;
    private transient DQLQueryCompilation _datastoreCompilation;

    @SuppressWarnings({"unchecked", "rawtypes" })
    public JPQLQuery(final StoreManager storeMgr, final ExecutionContext ec) {
        super(storeMgr, ec);
        _queryHelper = new DQLQueryHelper(this);
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    public JPQLQuery(final StoreManager storeMgr, final ExecutionContext ec,
            final AbstractJPQLQuery q) {
        super(storeMgr, ec, q);
        _queryHelper = new DQLQueryHelper(this);
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    public JPQLQuery(final StoreManager storeMgr, final ExecutionContext ec,
            final String query) {
        super(storeMgr, ec, query);
        _queryHelper = new DQLQueryHelper(this);
    }

    @Override
    protected void discardCompiled() {
        super.discardCompiled();
        _datastoreCompilation = null;
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

    @Override
    @SuppressWarnings({"unchecked", "rawtypes" })
    protected synchronized void compileInternal(final Map parameterValues) {
        if (isCompiled()) {
            return;
        }

        super.compileInternal(parameterValues);

        boolean inMemory = evaluateInMemory();
        if (candidateCollection != null && inMemory) {
            return;
        }

        if (candidateClass == null || candidateClassName == null) {
            candidateClass = compilation.getCandidateClass();
            candidateClassName = candidateClass.getName();
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
                setResultDistinct(compilation.getResultDistinct());
                return;
            }
        }

        _datastoreCompilation = new DQLQueryCompilation();
        AbstractClassMetaData cmd = getCandidateMetaData();

        ((StoreManagerImpl) storeMgr).manageClasses(ec, cmd.getFullClassName());

        if (!inMemory) {
            _queryHelper.compileQueryFull(parameterValues);
        }

        if (cacheKey != null) {
            if (_datastoreCompilation.isPrecompilable()) {
                qm.addDatastoreQueryCompilation(datastoreKey, getLanguage(),
                        cacheKey, _datastoreCompilation);
            }
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
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

}
