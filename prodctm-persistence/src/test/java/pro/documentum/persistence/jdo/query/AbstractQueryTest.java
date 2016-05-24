package pro.documentum.persistence.jdo.query;

import java.util.Map;

import javax.jdo.PersistenceManager;

import org.apache.commons.lang.StringUtils;
import org.datanucleus.api.jdo.JDOQuery;

import pro.documentum.model.jdo.AbstractPersistent;
import pro.documentum.persistence.common.query.DQLQuery;
import pro.documentum.persistence.jdo.JDOTestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@SuppressWarnings("rawtypes")
public class AbstractQueryTest extends JDOTestSupport {

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

}
