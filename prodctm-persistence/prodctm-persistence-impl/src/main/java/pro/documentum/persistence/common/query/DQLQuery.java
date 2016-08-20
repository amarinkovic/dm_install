package pro.documentum.persistence.common.query;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.query.AbstractJavaQuery;
import org.datanucleus.util.Localiser;
import org.datanucleus.util.NucleusLogger;

import com.documentum.fc.client.IDfSession;

import pro.documentum.persistence.common.util.DNQueries;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLQuery<R, E> extends AbstractJavaQuery<E> implements
        IDocumentumQuery<R> {

    private static final long serialVersionUID = -6611727771695158834L;

    private String _dql;

    public DQLQuery(final StoreManager storeMgr, final ExecutionContext ec) {
        this(storeMgr, ec, (String) null);
    }

    public DQLQuery(final StoreManager storeMgr, final ExecutionContext ec,
            final String dql) {
        super(storeMgr, ec);
        _dql = dql;
    }

    public DQLQuery(final StoreManager storeMgr, final ExecutionContext ec,
            final DQLQuery<R, E> dqlQuery) {
        super(storeMgr, ec);
        _dql = dqlQuery._dql;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void compileGeneric(final Map map) {

    }

    @Override
    public String getSingleStringQuery() {
        return _dql;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void compileInternal(final Map map) {

    }

    @Override
    @SuppressWarnings("rawtypes")
    protected Object performExecute(final Map map) {
        ManagedConnection mconn = getStoreManager().getConnection(ec);
        try {
            IDfSession session = (IDfSession) mconn.getConnection();
            long startTime = System.currentTimeMillis();
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(Localiser.msg("021046",
                        getLanguage(), getSingleStringQuery(), null));
            }
            List results = DNQueries.executeDqlQuery(this, session, _dql);
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(Localiser.msg("021074",
                        getLanguage(), ""
                                + (System.currentTimeMillis() - startTime)));
            }
            return results;
        } finally {
            mconn.release();
        }
    }

    @Override
    public Collection<R> getCandidateCollection() {
        return null;
    }

    @Override
    public String getCandidateAlias() {
        return null;
    }

    @Override
    public boolean evaluateInMemory() {
        return false;
    }

    @Override
    public AbstractClassMetaData getCandidateMetaData() {
        return super.getCandidateClassMetaData();
    }

    @Override
    public DQLQueryCompilation getDatastoreCompilation() {
        return null;
    }

}
