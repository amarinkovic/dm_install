package pro.documentum.jdo.query;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pro.documentum.jdo.JDOTestSupport;
import pro.documentum.model.AbstractPersistent;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class AbstractDQLMapperTest extends JDOTestSupport {

    protected String newQuery(Class<? extends AbstractPersistent> cls,
            String addon) {
        return newQuery(cls, addon, null);
    }

    protected String newQuery(Class<? extends AbstractPersistent> cls,
            String addon, Map<String, ?> params) {
        JDOQLQuery query;
        if (StringUtils.isBlank(addon)) {
            query = (JDOQLQuery) (((org.datanucleus.api.jdo.JDOQuery) getPersistenceManager()
                    .newQuery(cls)).getInternalQuery());
        } else {
            query = (JDOQLQuery) (((org.datanucleus.api.jdo.JDOQuery) getPersistenceManager()
                    .newQuery(cls, addon)).getInternalQuery());
        }
        if (params == null) {
            query.compile();
        } else {
            query.compileInternal(params);
        }
        return query.getNativeQuery();
    }

}
