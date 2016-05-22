package pro.documentum.jdo.query;

import java.util.Map;

import javax.jdo.PersistenceManager;

import org.apache.commons.lang.StringUtils;
import org.datanucleus.api.jdo.JDOQuery;

import pro.documentum.jdo.JDOTestSupport;
import pro.documentum.model.AbstractPersistent;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@SuppressWarnings("rawtypes")
public class AbstractQueryTest extends JDOTestSupport {

    protected DQLQuery dql(Class<? extends AbstractPersistent> cls, String query) {
        PersistenceManager pm = getPersistenceManager();
        JDOQuery q = (JDOQuery) pm.newQuery("DQL", query);
        q.setClass(cls);
        return (DQLQuery) q.getInternalQuery();
    }

    protected JDOQLQuery jdo(Class<? extends AbstractPersistent> cls,
            String addon) {
        PersistenceManager pm = getPersistenceManager();
        JDOQuery q = (JDOQuery) pm.newQuery(JDOQuery.JDOQL, null);
        q.setClass(cls);
        if (StringUtils.isNotBlank(addon)) {
            q.setFilter(addon);
        }
        return (JDOQLQuery) q.getInternalQuery();
    }

    protected static String str(JDOQLQuery query) {
        return str(query, null);
    }

    protected static String str(JDOQLQuery query, Map<String, ?> params) {
        if (params == null) {
            query.compile();
        } else {
            query.compileInternal(params);
        }
        return query.getNativeQuery();
    }

}
