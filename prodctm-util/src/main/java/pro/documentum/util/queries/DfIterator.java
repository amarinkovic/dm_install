package pro.documentum.util.queries;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DfIterator extends AbstractIterator {

    private IDfTypedObject _next;

    private IDfTypedObject _current;

    public DfIterator(final IDfCollection collection) {
        super(collection);
    }

    @Override
    public boolean hasNext() {
        if (_next != null) {
            return true;
        }

        if (getState() == IDfCollection.DF_CLOSED_STATE) {
            return false;
        }

        try {
            if (doNext()) {
                _next = getTypedObject();
                return true;
            } else {
                close();
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
            switch (getState()) {
            case IDfCollection.DF_INITIAL_STATE:
            case IDfCollection.DF_READY_STATE:
                throw new IllegalStateException("next() not called yet");
            case IDfCollection.DF_CLOSED_STATE:
                throw new IllegalStateException("collection closed");
            case IDfCollection.DF_NO_MORE_ROWS_STATE:
                throw new IllegalStateException("beyond end of collection");
            default:
                throw new IllegalStateException("Unknown state, state: "
                        + getState());
            }
        }
        return _current;
    }

}
