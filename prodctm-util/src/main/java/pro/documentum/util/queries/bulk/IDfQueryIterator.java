package pro.documentum.util.queries.bulk;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;

import pro.documentum.util.queries.IDfCollectionIterator;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class IDfQueryIterator implements Iterator<IDfTypedObject>,
        Closeable {

    private IDfCollectionIterator _iterator;

    public IDfQueryIterator(final IDfSession session, final String query) {
        try {
            _iterator = new IDfCollectionIterator(getQuery(query).execute(
                    session, IDfQuery.DF_EXEC_QUERY));
        } catch (DfException ex) {
            throw new RuntimeException(ex);
        }
    }

    private IDfQuery getQuery(final String query) {
        return new DfQuery(query);
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(_iterator);
        _iterator = null;
    }

    @Override
    public boolean hasNext() {
        if (_iterator != null && _iterator.hasNext()) {
            return true;
        }
        IOUtils.closeQuietly(this);
        return false;
    }

    @Override
    public IDfTypedObject next() {
        if (_iterator == null) {
            throw new IllegalStateException("Null iterator");
        }
        if (!hasNext()) {
            throw new IllegalStateException("Empty iterator");
        }
        return _iterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException(
                "This operation is not supported");
    }

}
