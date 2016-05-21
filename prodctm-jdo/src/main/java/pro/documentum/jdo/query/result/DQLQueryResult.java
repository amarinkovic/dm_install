package pro.documentum.jdo.query.result;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.query.AbstractQueryResult;
import org.datanucleus.store.query.AbstractQueryResultIterator;
import org.datanucleus.store.query.Query;
import org.datanucleus.util.Localiser;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.SoftValueMap;
import org.datanucleus.util.StringUtils;
import org.datanucleus.util.WeakValueMap;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;

import pro.documentum.jdo.util.DNFind;

public class DQLQueryResult<E> extends AbstractQueryResult<E> {

    private static final long serialVersionUID = -8682935620558424082L;

    private final ExecutionContext _ec;

    private final Map<Integer, E> _itemsByIndex;

    private List<CandidateClassResult> _results = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public DQLQueryResult(final Query<?> query) {
        super(query);
        _ec = query.getExecutionContext();

        String cacheType = query.getStringExtensionProperty("cacheType",
                "strong");
        if (cacheType == null) {
            _itemsByIndex = new WeakValueMap();
            return;
        }

        switch (cacheType.toLowerCase()) {
        case "soft":
            _itemsByIndex = new SoftValueMap();
            break;
        case "weak":
            _itemsByIndex = new WeakValueMap();
            break;
        case "strong":
            _itemsByIndex = new HashMap<>();
            break;
        case "none":
            _itemsByIndex = null;
            break;
        default:
            _itemsByIndex = new WeakValueMap();
        }
    }

    public void addCandidateResult(final AbstractClassMetaData cmd,
            final IDfCollection cursor, final int[] fpMembers)
        throws DfException {
        _results.add(new CandidateClassResult(cmd, cursor, fpMembers));
    }

    @Override
    protected void closingConnection() {
        if (!loadResultsAtCommit) {
            return;
        }
        if (!isOpen()) {
            return;
        }
        if (!hasNext()) {
            return;
        }
        NucleusLogger.QUERY.info(Localiser.msg("052606", query.toString()));
        loadRemainingResults();
    }

    private void loadRemainingResults() {
        if (!isOpen()) {
            return;
        }
        synchronized (this) {
            while (hasNext()) {
                getNextObject();
            }
        }
    }

    @Override
    public synchronized void close() {
        if (_itemsByIndex != null) {
            _itemsByIndex.clear();
        }
        for (CandidateClassResult result : _results) {
            result.close();
        }
        _results = null;
        super.close();
    }

    @Override
    protected void closeResults() {

    }

    @Override
    protected int getSizeUsingMethod() {
        if (!"LAST".equalsIgnoreCase(resultSizeMethod)) {
            return super.getSizeUsingMethod();
        }
        loadRemainingResults();
        size = _itemsByIndex.size();
        return size;
    }

    @Override
    public E get(final int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index must be 0 or higher");
        }

        if (_itemsByIndex != null && _itemsByIndex.containsKey(index)) {
            return _itemsByIndex.get(index);
        }

        while (true) {
            E nextPojo = getNextObject();
            // noinspection ConstantConditions
            if (_itemsByIndex.size() == (index + 1)) {
                return nextPojo;
            }
            if (!hasNext()) {
                throw new IndexOutOfBoundsException(
                        "Beyond size of the results (" + _itemsByIndex.size()
                                + ")");
            }
        }
    }

    protected boolean hasNext() {
        if (_results.isEmpty()) {
            return false;
        }
        Iterator<CandidateClassResult> iter = _results.iterator();
        while (iter.hasNext()) {
            CandidateClassResult result = iter.next();
            Iterator<IDfTypedObject> inner = result.iterator();
            if (inner.hasNext()) {
                return true;
            }
            iter.remove();
        }
        return false;
    }

    protected E getNextObject() {
        if (!hasNext()) {
            return null;
        }
        E pojo = null;
        Iterator<CandidateClassResult> iter = _results.iterator();
        outer: while (iter.hasNext()) {
            CandidateClassResult result = iter.next();
            // noinspection LoopStatementThatDoesntLoop
            for (IDfTypedObject dbObject : result) {
                pojo = getPojoForCandidate(result, dbObject);
                addObject(pojo);
                break outer;
            }
            iter.remove();
        }
        return pojo;
    }

    private E getPojoForCandidate(final CandidateClassResult result,
            final IDfTypedObject dbObject) {
        return DNFind.getPojoForDBObjectForCandidate(dbObject, _ec,
                result.getClassMetaData(), result.getMembers(),
                query.getIgnoreCache());
    }

    private void addObject(final E pojo) {
        _itemsByIndex.put(_itemsByIndex.size(), pojo);
    }

    @Override
    public boolean contains(final Object o) {
        loadRemainingResults();
        return _itemsByIndex.containsValue(o);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean containsAll(final Collection c) {
        loadRemainingResults();
        for (Object o : c) {
            if (!_itemsByIndex.containsKey(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<E> iterator() {
        return new QueryResultIterator();
    }

    @Override
    public ListIterator<E> listIterator() {
        return new QueryResultIterator();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof DQLQueryResult)) {
            return false;
        }

        DQLQueryResult<?> other = (DQLQueryResult) o;
        if (_results != null) {
            return other._results.equals(_results);
        } else if (query != null) {
            return other.query == query;
        }
        return StringUtils.toJVMIDString(other).equals(
                StringUtils.toJVMIDString(this));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    protected List<E> writeReplace() throws ObjectStreamException {
        disconnect();
        List<E> list = new ArrayList<>();
        for (int i = 0; i < _itemsByIndex.size(); i++) {
            list.add(_itemsByIndex.get(i));
        }
        return list;
    }

    private class QueryResultIterator extends AbstractQueryResultIterator<E> {

        private int _nextRowNum;

        QueryResultIterator() {
            this(0);
        }

        QueryResultIterator(final int nextRowNum) {
            _nextRowNum = nextRowNum;
        }

        @Override
        public boolean hasNext() {
            synchronized (DQLQueryResult.this) {
                if (!isOpen()) {
                    return false;
                }
                // noinspection SimplifiableIfStatement
                if (_nextRowNum < _itemsByIndex.size()) {
                    return true;
                }
                return DQLQueryResult.this.hasNext();
            }
        }

        @Override
        public E next() {
            synchronized (DQLQueryResult.this) {
                if (!isOpen()) {
                    throw new NoSuchElementException(Localiser.msg("052600"));
                }
                if (_nextRowNum < _itemsByIndex.size()) {
                    E pojo = _itemsByIndex.get(_nextRowNum);
                    ++_nextRowNum;
                    return pojo;
                }
                if (hasNext()) {
                    E pojo = getNextObject();
                    ++_nextRowNum;
                    return pojo;
                }
                throw new NoSuchElementException(Localiser.msg("052602"));
            }
        }

        @Override
        public boolean hasPrevious() {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public int nextIndex() {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public E previous() {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public int previousIndex() {
            throw new UnsupportedOperationException("Not yet implemented");
        }

    }

}
