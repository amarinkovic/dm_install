package pro.documentum.jdo.query;

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

import pro.documentum.jdo.util.DNFind;
import pro.documentum.util.queries.IDfCollectionIterator;
import pro.documentum.util.queries.Queries;

public class LazyLoadQueryResult<E> extends AbstractQueryResult<E> {

    private final ExecutionContext _executionContext;

    private List<CandidateClassResult> _results = new ArrayList<CandidateClassResult>();

    private Iterator<IDfTypedObject> _typedObjectIterator;

    private final Map<Integer, E> _itemsByIndex;

    private boolean _rangeProcessed;

    private boolean _orderProcessed;

    @SuppressWarnings("unchecked")
    public LazyLoadQueryResult(final Query query) {
        super(query);
        _executionContext = query.getExecutionContext();

        String cacheType = this.query.getStringExtensionProperty("cacheType",
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
            _itemsByIndex = new HashMap<Integer, E>();
            break;
        case "none":
            _itemsByIndex = null;
            break;
        default:
            _itemsByIndex = new WeakValueMap();
        }
    }

    public void addCandidateResult(final AbstractClassMetaData cmd,
            final IDfCollection cursor, final int[] fpMembers) {
        _results.add(new CandidateClassResult(cmd, cursor, fpMembers));
    }

    public void setRangeProcessed(final boolean processed) {
        _rangeProcessed = processed;
    }

    public boolean getRangeProcessed() {
        return _rangeProcessed;
    }

    public void setOrderProcessed(final boolean processed) {
        _orderProcessed = processed;
    }

    public boolean getOrderProcessed() {
        return _orderProcessed;
    }

    @Override
    protected void closingConnection() {
        if (!loadResultsAtCommit) {
            return;
        }
        if (!isOpen()) {
            return;
        }
        if (_results.isEmpty()) {
            return;
        }
        NucleusLogger.QUERY.info(Localiser.msg("052606", query.toString()));
        loadRemainingResults();
    }

    private void loadRemainingResults() {
        if (!isOpen()) {
            return;
        }
        if (_results.isEmpty()) {
            return;
        }

        synchronized (this) {
            if (_typedObjectIterator != null && !_results.isEmpty()) {
                CandidateClassResult result = _results.get(0);
                while (_typedObjectIterator.hasNext()) {
                    IDfTypedObject dbObject = _typedObjectIterator.next();
                    E pojo = DNFind.getPojoForDBObjectForCandidate(dbObject,
                            _executionContext, result._classMetaData,
                            result._fpmembers, query.getIgnoreCache());
                    _itemsByIndex.put(_itemsByIndex.size(), pojo);
                }
                Queries.close(result._collection);
                _results.remove(0);
                _typedObjectIterator = null;
            }

            Iterator<CandidateClassResult> candidateResultsIter = _results
                    .iterator();
            while (candidateResultsIter.hasNext()) {
                CandidateClassResult result = candidateResultsIter.next();
                _typedObjectIterator = new IDfCollectionIterator(
                        result._collection);
                while (_typedObjectIterator.hasNext()) {
                    IDfTypedObject dbObject = _typedObjectIterator.next();
                    E pojo = DNFind.getPojoForDBObjectForCandidate(dbObject,
                            _executionContext, result._classMetaData,
                            result._fpmembers, query.getIgnoreCache());
                    _itemsByIndex.put(_itemsByIndex.size(), pojo);
                }
                Queries.close(result._collection);
                candidateResultsIter.remove();
                _typedObjectIterator = null;
            }
        }
    }

    @Override
    public synchronized void close() {
        if (_itemsByIndex != null) {
            _itemsByIndex.clear();
        }
        for (CandidateClassResult result : _results) {
            Queries.close(result._collection);
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
        while (true) {
            getNextObject();
            if (!_results.isEmpty()) {
                continue;
            }
            size = _itemsByIndex.size();
            return size;
        }
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
            if (_itemsByIndex.size() == (index + 1)) {
                return nextPojo;
            }
            if (_results.isEmpty()) {
                throw new IndexOutOfBoundsException(
                        "Beyond size of the results (" + _itemsByIndex.size()
                                + ")");
            }
        }
    }

    protected E getNextObject() {
        if (_results.isEmpty()) {
            return null;
        }

        E pojo = null;
        CandidateClassResult result = _results.get(0);

        if (_typedObjectIterator != null) {
            IDfTypedObject dbObject = _typedObjectIterator.next();
            pojo = DNFind.getPojoForDBObjectForCandidate(dbObject,
                    _executionContext, result._classMetaData,
                    result._fpmembers, query.getIgnoreCache());
            _itemsByIndex.put(_itemsByIndex.size(), pojo);

            if (!_typedObjectIterator.hasNext()) {
                Queries.close(result._collection);
                _typedObjectIterator = null;
                _results.remove(result);
            }
        } else {
            boolean noNextResult = true;
            while (noNextResult) {
                _typedObjectIterator = new IDfCollectionIterator(
                        result._collection);
                if (_typedObjectIterator.hasNext()) {
                    IDfTypedObject dbObject = _typedObjectIterator.next();
                    pojo = DNFind.getPojoForDBObjectForCandidate(dbObject,
                            _executionContext, result._classMetaData,
                            result._fpmembers, query.getIgnoreCache());
                    _itemsByIndex.put(_itemsByIndex.size(), pojo);
                    noNextResult = false;

                    if (!_typedObjectIterator.hasNext()) {
                        Queries.close(result._collection);
                        _typedObjectIterator = null;
                        _results.remove(result);
                    }
                } else {
                    Queries.close(result._collection);
                    _typedObjectIterator = null;
                    _results.remove(result);
                    if (_results.isEmpty()) {
                        noNextResult = false;
                        pojo = null;
                    } else {
                        result = _results.get(0);
                    }
                }
            }
        }

        return pojo;
    }

    @Override
    public boolean contains(final Object o) {
        loadRemainingResults();
        return _itemsByIndex.containsValue(o);
    }

    @Override
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
        if (o == null || !(o instanceof LazyLoadQueryResult)) {
            return false;
        }

        LazyLoadQueryResult other = (LazyLoadQueryResult) o;
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
        List<E> list = new ArrayList<E>();
        for (int i = 0; i < _itemsByIndex.size(); i++) {
            list.add(_itemsByIndex.get(i));
        }
        return list;
    }

    private static class CandidateClassResult {

        private final AbstractClassMetaData _classMetaData;
        private final IDfCollection _collection;
        private final int[] _fpmembers;

        CandidateClassResult(final AbstractClassMetaData classMetaData,
                final IDfCollection curs, final int[] fpMemberPositions) {
            _classMetaData = classMetaData;
            _collection = curs;
            _fpmembers = fpMemberPositions;
        }
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
            synchronized (LazyLoadQueryResult.this) {
                if (!isOpen()) {
                    return false;
                }
                if (_nextRowNum < _itemsByIndex.size()) {
                    return true;
                }
                return !_results.isEmpty();
            }
        }

        @Override
        public E next() {
            synchronized (LazyLoadQueryResult.this) {
                if (!isOpen()) {
                    throw new NoSuchElementException(Localiser.msg("052600"));
                }

                if (_nextRowNum < _itemsByIndex.size()) {
                    E pojo = _itemsByIndex.get(_nextRowNum);
                    ++_nextRowNum;
                    return pojo;
                } else if (!_results.isEmpty()) {
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
