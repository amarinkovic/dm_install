package pro.documentum.jdo.query;

import java.util.Map;

import org.datanucleus.ExecutionContext;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.query.AbstractJPQLQuery;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class JPQLQuery extends AbstractJPQLQuery {

    public JPQLQuery(final StoreManager storeMgr, final ExecutionContext ec) {
        super(storeMgr, ec);
    }

    public JPQLQuery(final StoreManager storeMgr, final ExecutionContext ec,
            final AbstractJPQLQuery q) {
        super(storeMgr, ec, q);
    }

    public JPQLQuery(final StoreManager storeMgr, final ExecutionContext ec,
            final String query) {
        super(storeMgr, ec, query);
    }

    @Override
    protected Object performExecute(final Map map) {
        return null;
    }

}
