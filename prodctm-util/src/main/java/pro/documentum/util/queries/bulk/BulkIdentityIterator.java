package pro.documentum.util.queries.bulk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

import pro.documentum.util.queries.Queries;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class BulkIdentityIterator<O extends IDfPersistentObject> extends
        AbstractBulkIterator<O, Iterator<String>> implements
        IQueryBuilder<List<String>> {

    public BulkIdentityIterator(final IDfSession session,
            final String objectType, final Collection<String> ids) {
        this(session, objectType, ids, null);
    }

    public BulkIdentityIterator(final IDfSession session,
            final String objectType, final Collection<String> keys,
            final IConsistencyChecker consistencyChecker) {
        this(session, objectType, keys.iterator(), consistencyChecker);
    }

    public BulkIdentityIterator(final IDfSession session,
            final String objectType, final Iterator<String> keys,
            final IConsistencyChecker consistencyChecker) {
        super(session, objectType, keys, consistencyChecker);
    }

    public static Iterator<IDfPersistentObject> select(
            final IDfSession session, final String objectType,
            final List<String> keys) throws DfException {
        Map<String, IDfPersistentObject> objects = new HashMap<>();
        IBulkIterator<?> iterator = null;
        try {
            iterator = new BulkIdentityIterator<>(session, objectType, keys);
            while (iterator.hasNext()) {
                IDfPersistentObject object = iterator.next();
                objects.put(object.getObjectId().getId(), object);
            }
            List<IDfPersistentObject> result = new ArrayList<>(keys.size());
            for (String key : keys) {
                result.add(objects.remove(key));
            }
            return result.iterator();
        } finally {
            IOUtils.closeQuietly(iterator);
        }
    }

    @Override
    protected Iterator<String> getQueries(final Iterator<String> ids) {
        return new QueryIterator<>(this, ids);
    }

    public String buildQuery(final List<String> ids) {
        try {
            List<String> attributes = getAttributes();
            StringBuilder query = new StringBuilder(attributes.size() * 16
                    + ids.size() * 16);
            query.append("SELECT ").append(getProjection(attributes));
            query.append(" FROM ").append(getObjectType());
            query.append(getTypeModifier(attributes));
            query.append(" WHERE ");
            query.append(Queries.createInClause(ids));
            query.append(" ").append(getOrderBy(attributes));
            return query.toString();
        } catch (DfException ex) {
            throw new RuntimeException(ex);
        }
    }

}
