package pro.documentum.util.queries.bulk;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfDocbaseConstants;
import com.documentum.fc.common.DfException;

import pro.documentum.util.queries.Queries;
import pro.documentum.util.queries.keys.CompositeKey;
import pro.documentum.util.queries.keys.KeyFactory;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class BulkCompositeKeyIterator<K extends CompositeKey, O extends IDfPersistentObject>
        extends AbstractBulkIterator<O, Iterator<K>> implements
        IQueryBuilder<List<K>> {

    public BulkCompositeKeyIterator(final IDfSession session,
            final String objectType, final Collection<K> param) {
        this(session, objectType, param, null);
    }

    public BulkCompositeKeyIterator(final IDfSession session,
            final String objectType, final Collection<K> param,
            final IConsistencyChecker consistencyChecker) {
        this(session, objectType, param.iterator(), consistencyChecker);
    }

    public BulkCompositeKeyIterator(final IDfSession session,
            final String objectType, final Iterator<K> param,
            final IConsistencyChecker consistencyChecker) {
        super(session, objectType, param, consistencyChecker);
    }

    public static <T extends CompositeKey> Iterator<IDfPersistentObject> select(
            final IDfSession session, final String objectType,
            final List<T> keys) throws DfException {
        Map<T, List<IDfPersistentObject>> objects = new HashMap<>();
        try (IBulkIterator<?> iterator = new BulkCompositeKeyIterator<>(
                session, objectType, keys)) {
            List<String> columns = null;
            while (iterator.hasNext()) {
                if (columns == null) {
                    columns = keys.get(0).getKeys();
                }
                IDfPersistentObject object = iterator.next();
                T key = KeyFactory.createKey(object, columns);
                if (!objects.containsKey(key)) {
                    objects.put(key, new LinkedList<IDfPersistentObject>());
                }
                objects.get(key).add(object);
            }
            return new BulkResultIterator<>(keys, objects);
        } catch (IOException ex) {
            throw new DfException(ex);
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes" })
    protected Iterator<String> getQueries(final Iterator<K> param) {
        return new QueryIterator(this, param, 10);
    }

    @Override
    public String buildQuery(final List<K> param) {
        try {
            String attrName = getSingleAttr(param);
            List<String> attributes = getAttributes();
            StringBuilder query = new StringBuilder(attributes.size() * 16
                    + param.size() * 32);
            query.append("SELECT ").append(getProjection(attributes));
            query.append(" FROM ").append(getObjectType());
            if (DfDocbaseConstants.R_OBJECT_ID.equals(attrName)) {
                query.append(getTypeModifier(attributes));
            }
            query.append(" WHERE ");
            if (StringUtils.isNotBlank(attrName)) {
                query.append(Queries.createClause(attrName, param));
            } else {
                query.append(Queries.createClause(param));
            }
            query.append(" ").append(getOrderBy(attributes));
            return query.toString();
        } catch (DfException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected String getSingleAttr(final List<K> param) {
        boolean isSimple = true;
        String attrName = null;
        for (CompositeKey key : param) {
            if (attrName == null) {
                attrName = key.getFirstAttr();
            }
            if (!key.isOnlyAttr(attrName)) {
                isSimple = false;
                break;
            }
        }
        if (isSimple) {
            return attrName;
        }
        return null;
    }

}
