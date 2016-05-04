package pro.documentum.util.queries;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class IDfCollectionIterator implements Iterator<IDfTypedObject>,
        Closeable {

    private IDfTypedObject _next;

    private IDfTypedObject _current;

    private final IDfCollection _collection;

    public IDfCollectionIterator(final IDfCollection collection) {
        _collection = Objects.requireNonNull(collection);
    }

    @Override
    public boolean hasNext() {
        if (_next != null) {
            return true;
        }

        if (_collection.getState() == IDfCollection.DF_CLOSED_STATE) {
            return false;
        }

        try {
            if (_collection.next()) {
                _next = _collection.getTypedObject();
                return true;
            } else {
                Queries.close(_collection);
                return false;
            }
        } catch (DfException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public IDfTypedObject next() {
        if (hasNext()) {
            _current = _next;
            _next = null;
        } else {
            _next = null;
            _current = null;
        }
        return getCurrent();
    }

    @Override
    public void remove() {
        // do nothing
    }

    private IDfTypedObject getCurrent() {
        if (_current == null) {
            switch (_collection.getState()) {
            case IDfCollection.DF_INITIAL_STATE:
            case IDfCollection.DF_READY_STATE:
                throw new IllegalStateException("next() not called yet");
            case IDfCollection.DF_CLOSED_STATE:
                throw new IllegalStateException("collection closed");
            case IDfCollection.DF_NO_MORE_ROWS_STATE:
                throw new IllegalStateException("beyond end of collection");
            default:
                throw new IllegalStateException("Unknown state, state: "
                        + _collection.getState());
            }
        }
        return _current;
    }

    @Override
    public void close() throws IOException {
        Queries.close(_collection);
    }

}
