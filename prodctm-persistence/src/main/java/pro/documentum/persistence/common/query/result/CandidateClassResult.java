package pro.documentum.persistence.common.query.result;

import java.io.Closeable;
import java.util.Iterator;

import org.datanucleus.ExecutionContext;

import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;

import pro.documentum.util.queries.DfIterator;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
class CandidateClassResult<T> implements Iterable<IDfTypedObject>, Closeable {

    private final DfIterator _iterator;

    private final IResultObjectFactory<T> _objectFactory;

    CandidateClassResult(final DfIterator curs,
            final IResultObjectFactory<T> objectFactory) throws DfException {
        _iterator = curs;
        _objectFactory = objectFactory;
    }

    @Override
    public Iterator<IDfTypedObject> iterator() {
        return _iterator;
    }

    @Override
    public void close() {
        _iterator.close();
    }

    public T getPojoForCandidate(final ExecutionContext context,
            final IDfTypedObject dbObject) {
        return _objectFactory.getObject(context, dbObject);
    }

}
