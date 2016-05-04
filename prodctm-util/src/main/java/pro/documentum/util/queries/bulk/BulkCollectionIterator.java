package pro.documentum.util.queries.bulk;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfDocbaseConstants;
import com.documentum.fc.common.DfException;

import pro.documentum.util.queries.Queries;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class BulkCollectionIterator<O extends IDfPersistentObject> extends
        AbstractBulkIterator<O, Iterator<String>> implements
        IQueryBuilder<List<String>> {

    private final String _key;

    public BulkCollectionIterator(final IDfSession session,
            final String objectType, final Collection<String> keys) {
        this(session, DfDocbaseConstants.R_OBJECT_ID, objectType, keys, null);
    }

    public BulkCollectionIterator(final IDfSession session,
            final String objectType, final Collection<String> keys,
            final IConsistencyChecker consistencyChecker) {
        this(session, DfDocbaseConstants.R_OBJECT_ID, objectType, keys,
                consistencyChecker);
    }

    public BulkCollectionIterator(final IDfSession session, final String key,
            final String objectType, final Collection<String> keys,
            final IConsistencyChecker consistencyChecker) {
        this(session, key, objectType, keys.iterator(), consistencyChecker);
    }

    public BulkCollectionIterator(final IDfSession session,
            final String objectType, final Iterator<String> keys,
            final IConsistencyChecker consistencyChecker) {
        this(session, DfDocbaseConstants.R_OBJECT_ID, objectType, keys,
                consistencyChecker);
    }

    public BulkCollectionIterator(final IDfSession session, final String key,
            final String objectType, final Iterator<String> keys,
            final IConsistencyChecker consistencyChecker) {
        super(session, objectType, keys, consistencyChecker);
        _key = key;
    }

    @Override
    protected Iterator<String> getQueries(final Iterator<String> keys) {
        return new QueryIterator(this, keys);
    }

    public String buildQuery(final List<String> keys) {
        try {
            List<String> attributes = getAttributes();
            StringBuilder query = new StringBuilder(attributes.size() * 16
                    + keys.size() * 16);
            query.append("SELECT ").append(getProjection(attributes));
            query.append(" FROM ").append(getObjectType());
            query.append(getTypeModifier(attributes));
            query.append(" WHERE ");
            query.append(Queries.createInClause(_key, keys));
            query.append(" ").append(getOrderBy(attributes));
            return query.toString();
        } catch (DfException ex) {
            throw new RuntimeException(ex);
        }
    }

}
