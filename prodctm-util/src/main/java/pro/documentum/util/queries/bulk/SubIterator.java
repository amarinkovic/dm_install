package pro.documentum.util.queries.bulk;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class SubIterator<T> implements Iterator<List<T>>, Closeable {

    private final int _limit;
    private Iterator<T> _iterator;

    public SubIterator(final Iterator<T> collection, final int limit) {
        _iterator = collection;
        _limit = limit;
    }

    public SubIterator(final Iterable<T> collection, final int limit) {
        this(collection.iterator(), limit);
    }

    @Override
    public void close() throws IOException {
        if (_iterator instanceof Closeable) {
            IOUtils.closeQuietly((Closeable) _iterator);
        }
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
    public List<T> next() {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < _limit && hasNext(); i++) {
            result.add(_iterator.next());
        }
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException(
                "This operation is not supported");
    }

}
