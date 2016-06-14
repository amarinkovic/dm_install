package pro.documentum.util.queries.bulk;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class BulkQueryIterator<O extends IDfPersistentObject> extends
        AbstractBulkIterator<O, String> {

    public BulkQueryIterator(final IDfSession session, final String objectType,
            final String query, final IConsistencyChecker consistencyChecker)
        throws DfException {
        super(session, objectType, query, consistencyChecker);
    }

    @Override
    protected Iterator<String> getQueries(final String param) {
        try {
            List<String> attributes = getAttributes();
            String projection = getProjection(attributes);
            StringBuilder query = new StringBuilder(attributes.size() * 16
                    + param.length());
            query.append("SELECT ").append(projection);
            query.append(" FROM ").append(getObjectType());
            query.append(getTypeModifier(attributes));
            query.append(" WHERE r_object_id IN (");
            query.append(param).append(") ");
            query.append(getOrderBy(attributes));
            return Collections.singletonList(query.toString()).iterator();
        } catch (DfException ex) {
            throw new RuntimeException(ex);
        }
    }

}
