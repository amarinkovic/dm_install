package pro.documentum.jdo.query.result;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ColumnMetaData;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;

import pro.documentum.util.queries.IDfCollectionIterator;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
class CandidateClassResult implements Iterable<IDfTypedObject>, Closeable {

    private final AbstractClassMetaData _classMetaData;

    private final IDfCollectionIterator _iterator;

    private final int[] _members;

    CandidateClassResult(final AbstractClassMetaData classMetaData,
            final IDfCollection curs, final int[] fpMemberPositions)
        throws DfException {
        _classMetaData = classMetaData;
        _iterator = new IDfCollectionIterator(curs);
        List<Integer> members = new ArrayList<>();
        for (int position : fpMemberPositions) {
            AbstractMemberMetaData mmd = _classMetaData
                    .getMetaDataForManagedMemberAtAbsolutePosition(position);
            boolean exists = true;
            for (ColumnMetaData cmd : mmd.getColumnMetaData()) {
                String column = cmd.getName();
                if (!curs.hasAttr(column)) {
                    exists = false;
                    break;
                }
            }
            if (exists) {
                members.add(position);
            }
        }
        _members = new int[members.size()];
        for (int i = 0, n = members.size(); i < n; i++) {
            _members[i] = members.get(i);
        }
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
