package pro.documentum.persistence.jpa.query;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.datanucleus.api.jpa.JPAQuery;

import pro.documentum.model.jpa.AbstractPersistent;
import pro.documentum.persistence.jpa.JPATestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@SuppressWarnings("rawtypes")
public class AbstractQueryTest extends JPATestSupport {

    protected static String str(JPQLQuery query) {
        return str(query, null);
    }

    protected static String str(JPQLQuery query, Map<String, ?> params) {
        if (params == null) {
            query.compile();
        } else {
            query.compileInternal(params);
        }
        return query.getNativeQuery();
    }

    protected JPQLQuery jpa(Class<? extends AbstractPersistent> cls,
            String addon) {
        CriteriaBuilder cb = getCriteriaBuilder();
        CriteriaQuery<?> crit = cb.createQuery(cls);
        Root<?> candidateRoot = crit.from(cls);
        candidateRoot.alias("this");
        StringBuilder builder = new StringBuilder(crit.toString());
        if (StringUtils.isNotBlank(addon)) {
            builder.append(" WHERE ").append(addon);
        }
        return (JPQLQuery) ((JPAQuery) getEntityManager().createQuery(
                builder.toString())).getInternalQuery();
    }

    protected CriteriaBuilder getCriteriaBuilder() {
        EntityManagerFactory entityManagerFactory = getEntityManagerFactory();
        return entityManagerFactory.getCriteriaBuilder();
    }

}
