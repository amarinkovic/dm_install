package pro.documentum.util.queries.bulk;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.IOUtils;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfDocbaseConstants;
import com.documentum.fc.common.DfException;

import pro.documentum.util.objects.DfObjects;
import pro.documentum.util.queries.ReservedWords;
import pro.documentum.util.types.DfTypes;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractBulkIterator<O extends IDfPersistentObject, T>
        implements IBulkIterator<O> {

    private final IDfSession _session;

    private final String _objectType;

    private final IConsistencyChecker _consistencyChecker;

    private IDfQueriesIterator _iterator;

    private IDfTypedObject _next;

    public AbstractBulkIterator(final IDfSession session,
            final String objectType, final T param,
            final IConsistencyChecker consistencyChecker) {
        _session = session;
        _objectType = objectType;
        _consistencyChecker = consistencyChecker;
        _iterator = new IDfQueriesIterator(_session, getQueries(param));
    }

    @Override
    public boolean hasNext() {
        try {
            if (_next != null) {
                return true;
            }
            while (_iterator != null && _iterator.hasNext()) {
                IDfTypedObject object = _iterator.next();
                if (skip(object)) {
                    continue;
                }
                _next = object;
                return true;
            }
            IOUtils.closeQuietly(this);
            return false;
        } catch (DfException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public O next() {
        try {
            IDfTypedObject current = null;
            if (hasNext()) {
                current = _next;
            }
            _next = null;
            Objects.requireNonNull(current, "Empty iterator");
            String typeName = _objectType;
            if (current.hasAttr(DfDocbaseConstants.R_OBJECT_TYPE)) {
                typeName = current.getString(DfDocbaseConstants.R_OBJECT_TYPE);
            }
            return (O) DfObjects.asPersistent(_session, current, typeName);
        } catch (DfException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException(
                "This operation is not supported");
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(_iterator);
        _iterator = null;
    }

    protected abstract Iterator<String> getQueries(final T param);

    protected final boolean skip(final IDfTypedObject object)
        throws DfException {
        if (_consistencyChecker == null) {
            return false;
        }
        if (!object.hasAttr(DfDocbaseConstants.R_OBJECT_ID)) {
            return false;
        }
        String objectId = object.getString(DfDocbaseConstants.R_OBJECT_ID);
        return _consistencyChecker.skip(objectId);
    }

    protected final IDfSession getSession() {
        return _session;
    }

    protected final String getObjectType() {
        return _objectType;
    }

    protected final List<String> getAttributes() throws DfException {
        return DfTypes.getAttributes(_session, _objectType);
    }

    protected final String getProjection(final List<String> attributes) {
        return ReservedWords.makeProjection(attributes);
    }

    protected final String getOrderBy(final List<String> attributes) {
        StringBuilder builder = new StringBuilder(2 * 16);
        builder.append("ORDER BY ").append(DfDocbaseConstants.R_OBJECT_ID);
        if (attributes.contains("i_position")) {
            builder.append(", i_position DESC");
        }
        return builder.toString();
    }

    protected final String getTypeModifier(final List<String> attributes) {
        if (attributes.contains(DfDocbaseConstants.I_CHRONICLE_ID)) {
            return "(ALL)";
        }
        return "";
    }

}
