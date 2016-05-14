package pro.documentum.jdo.query.result;

import java.io.Closeable;
import java.util.Iterator;

import org.datanucleus.metadata.AbstractClassMetaData;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfTypedObject;

import pro.documentum.util.queries.IDfCollectionIterator;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
class CandidateClassResult implements Iterable<IDfTypedObject>, Closeable {

    private final AbstractClassMetaData _classMetaData;

    private final IDfCollectionIterator _iterator;

    private final int[] _members;

    CandidateClassResult(final AbstractClassMetaData classMetaData,
            final IDfCollection curs, final int[] fpMemberPositions) {
        _classMetaData = classMetaData;
        _iterator = new IDfCollectionIterator(curs);
        _members = fpMemberPositions;
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
