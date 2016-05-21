package pro.documentum.util.queries.bulk;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
final class QueryIterator implements Iterator<String>, Closeable {

    public static final int MAX_OBJECTS_IN_QUERY = 500;

    private final IQueryBuilder<List<String>> _queryBuilder;

    private SubIterator<String> _iterator;

    QueryIterator(final IQueryBuilder<List<String>> queryBuilder,
            final Iterator<String> keys) {
        _queryBuilder = queryBuilder;
        _iterator = new SubIterator<>(keys, MAX_OBJECTS_IN_QUERY);
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
    public String next() {
        if (hasNext()) {
            return _queryBuilder.buildQuery(_iterator.next());
        }
        throw new IllegalStateException("Empty iterator");
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException(
                "This operation is not supported");
    }

}
