package pro.documentum.persistence.common.query.result;

import java.io.Closeable;
import java.util.Iterator;

import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;

import pro.documentum.util.queries.DfIterator;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
class CandidateClassResult<T> implements Iterable<IDfTypedObject>, Closeable {

    private final DfIterator _cursor;

    private final IResultFactory<T> _resultFactory;

    CandidateClassResult(final DfIterator cursor,
            final IResultFactory<T> resultFactory) throws DfException {
        _cursor = cursor;
        _resultFactory = resultFactory;
    }

    @Override
    public Iterator<IDfTypedObject> iterator() {
        return _cursor;
    }

    @Override
    public void close() {
        _cursor.close();
    }

    public T getObject(final IDfTypedObject dbObject) {
        return _resultFactory.getObject(dbObject);
    }

}
