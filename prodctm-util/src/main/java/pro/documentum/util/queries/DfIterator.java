package pro.documentum.util.queries;

import java.io.Closeable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfAttr;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DfIterator implements Iterator<IDfTypedObject>, Closeable {

    private final IDfCollection _collection;
    private IDfTypedObject _next;
    private IDfTypedObject _current;

    public DfIterator(final IDfCollection collection) {
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
    public void close() {
        Queries.close(_collection);
    }

    public IDfAttr getAttr(final int index) throws DfException {
        return _collection.getAttr(index);
    }

    public int getAttrCount() throws DfException {
        return _collection.getAttrCount();
    }

    public int getAttrDataType(final String attrName) throws DfException {
        return _collection.getAttrDataType(attrName);
    }

    public boolean hasAttr(final String attrName) throws DfException {
        return _collection.hasAttr(attrName);
    }

    @SuppressWarnings("rawtypes")
    public Enumeration enumAttrs() throws DfException {
        return _collection.enumAttrs();
    }

    public int findAttrIndex(final String attrName) throws DfException {
        return _collection.findAttrIndex(attrName);
    }

    public boolean isAttrRepeating(final String attrName) throws DfException {
        return _collection.isAttrRepeating(attrName);
    }

}
