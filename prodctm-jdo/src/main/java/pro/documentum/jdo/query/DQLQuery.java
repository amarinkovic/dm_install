package pro.documentum.jdo.query;

import java.util.Map;

import org.datanucleus.ExecutionContext;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.query.AbstractJavaQuery;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLQuery extends AbstractJavaQuery {

    public DQLQuery(final StoreManager storeMgr, final ExecutionContext ec) {
        super(storeMgr, ec);
    }

    @Override
    public void compileGeneric(final Map map) {

    }

    @Override
    public String getSingleStringQuery() {
        return null;
    }

    @Override
    protected void compileInternal(final Map map) {

    }

    @Override
    protected Object performExecute(final Map map) {
        return null;
    }

}
