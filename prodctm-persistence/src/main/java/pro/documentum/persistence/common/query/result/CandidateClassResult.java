package pro.documentum.persistence.common.query.result;

import java.io.Closeable;
import java.util.Iterator;

import org.datanucleus.metadata.AbstractClassMetaData;

import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;

import pro.documentum.util.queries.DfIterator;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
class CandidateClassResult implements Iterable<IDfTypedObject>, Closeable {

    private final AbstractClassMetaData _classMetaData;

    private final DfIterator _iterator;

    private final int[] _members;

    CandidateClassResult(final AbstractClassMetaData classMetaData,
            final DfIterator curs, final int[] fpMembers) throws DfException {
        _classMetaData = classMetaData;
        _iterator = curs;
        _members = fpMembers;
    }

    public AbstractClassMetaData getClassMetaData() {
        return _classMetaData;
    }

    public int[] getMembers() {
        return _members;
    }

    @Override
    public Iterator<IDfTypedObject> iterator() {
        return _iterator;
    }

    @Override
    public void close() {
        _iterator.close();
    }

}
