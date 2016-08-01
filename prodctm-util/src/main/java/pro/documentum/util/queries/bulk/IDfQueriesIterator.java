package pro.documentum.util.queries.bulk;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

import org.apache.commons.io.IOUtils;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfTypedObject;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class IDfQueriesIterator implements Iterator<IDfTypedObject>, Closeable {

    private final IDfSession _session;

    private Iterator<String> _queries;

    private IDfQueryIterator _iterator;

    public IDfQueriesIterator(final IDfSession session,
            final Iterator<String> queries) {
        _session = session;
        _queries = queries;
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(_iterator);
        _iterator = null;
        if (_queries instanceof Closeable) {
            IOUtils.closeQuietly((Closeable) _queries);
        }
        _queries = null;
    }

    @Override
    public boolean hasNext() {
        if (_iterator != null && _iterator.hasNext()) {
            return true;
        }
        if (_iterator != null) {
            IOUtils.closeQuietly(_iterator);
            _iterator = null;
        }
        while (_queries != null && _queries.hasNext()) {
            _iterator = new IDfQueryIterator(_session, _queries.next());
            if (_iterator.hasNext()) {
                return true;
            }
            IOUtils.closeQuietly(_iterator);
            _iterator = null;
        }
        IOUtils.closeQuietly(this);
        return false;
    }

    @Override
    public IDfTypedObject next() {
        Objects.requireNonNull(_iterator, "Null iterator");
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
